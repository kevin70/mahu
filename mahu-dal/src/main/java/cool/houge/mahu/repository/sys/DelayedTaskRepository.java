package cool.houge.mahu.repository.sys;

import com.github.f4b6a3.ulid.UlidFactory;
import cool.houge.mahu.config.Status;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.entity.sys.query.QDelayedTask;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.ebean.TransactionCallbackAdapter;
import io.helidon.common.context.Contexts;
import io.helidon.service.registry.Service;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

/// 延时任务仓库
///
/// 用于集中封装 delayed_tasks 的“入队/领取/超时回收/终结态转换”等数据库操作。
/// 其中 claim/transition/complete/archive 均采用条件更新（WHERE status=? 等），用于集群环境下的并发安全。
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
public class DelayedTaskRepository extends HBeanRepository<UUID, DelayedTask> {

    private static final Logger log = LogManager.getLogger(DelayedTaskRepository.class);
    private static final UlidFactory ULID_FACTORY = UlidFactory.newMonotonicInstance();

    /// 与 {@link #claimPending} 中 `lease_seconds` 为 null 时的默认租约一致（秒）。
    public static final int DEFAULT_LEASE_SECONDS = 60;

    /// 租约到期条件（查询场景），与 `lock_at + lease <= now` 等价：`lock_at <= now - lease`（PostgreSQL）。
    /// 说明：显式转 `?::timestamptz`，避免驱动在表达式中将参数推断为错误类型。
    private static final String LEASE_EXPIRED_QUERY_PREDICATE =
            "t0.lock_at <= (?::timestamptz - (interval '1 second' * " + "COALESCE(t0.lease_seconds, "
                    + DEFAULT_LEASE_SECONDS + ")))";

    /// 租约到期条件（更新场景），不能引用查询别名（如 `t0`）。
    private static final String LEASE_EXPIRED_UPDATE_PREDICATE = "lock_at <= (?::timestamptz - (interval '1 second' * "
            + "COALESCE(lease_seconds, " + DEFAULT_LEASE_SECONDS + ")))";

    public DelayedTaskRepository(Database db) {
        super(DelayedTask.class, db);
    }

    private static void requireNonBlankReferenceId(String referenceId) {
        Objects.requireNonNull(referenceId, "referenceId");
        if (referenceId.isBlank()) {
            throw new IllegalArgumentException("referenceId must not be blank");
        }
    }

    /// 入队延时任务
    ///
    /// 行为说明：
    /// - 强制校验 `referenceId` 非空白
    /// - 始终在仓储侧生成 ULID UUID，避免调用方自行分配主键
    /// - 若存在 Helidon Context，则延迟到事务提交前批量落库；否则立即落库
    public void enqueueDelayedTask(DelayedTask task) {
        requireNonBlankReferenceId(task.getReferenceId());
        task.setId(ULID_FACTORY.create().toUuid());

        var ctx = Contexts.context();
        if (ctx.isEmpty()) {
            this.save(task);
            log.debug("持久化延时任务: {}", task);
        } else {
            ctx.get().get(DelayedTaskHolder.class).ifPresentOrElse(holder -> holder.add(task), () -> {
                var cb = new DelayedTaskHolder(this).add(task);
                ctx.get().register(cb);
                db().register(cb);
            });
            log.debug("保存延时任务到 Context: {}", task);
        }
    }

    /// 分页查询延时任务
    ///
    /// `topic` 为空白时不加过滤条件。
    public PagedList<DelayedTask> findPage(@Nullable String topic, Page page) {
        var qb = new QDelayedTask(db());
        if (topic != null && !topic.isBlank()) {
            qb.topic.eq(topic);
        }
        return super.findPage(qb, page);
    }

    /// 查询到期且待处理（PENDING）的任务。
    public List<DelayedTask> findDuePending(Instant now, int limit) {
        return newDuePendingQuery(now, limit).findList();
    }

    /// 查询租约超时且处理中（PROCESSING）的任务。
    public List<DelayedTask> findExpiredProcessing(Instant now, int limit) {
        return newExpiredProcessingQuery(now, limit).findList();
    }

    /// 与 {@link #findDuePending} 语义一致，但使用 `FOR UPDATE SKIP LOCKED` 规避并发竞争。
    public List<DelayedTask> findDuePendingSkipLocked(Instant now, int limit) {
        var qb = newDuePendingQuery(now, limit);
        qb.delayUntil.asc();
        qb.forUpdateSkipLocked();
        return qb.findList();
    }

    /// 与 {@link #findExpiredProcessing} 语义一致，但使用 `FOR UPDATE SKIP LOCKED`。
    public List<DelayedTask> findExpiredProcessingSkipLocked(Instant now, int limit) {
        var qb = newExpiredProcessingQuery(now, limit);
        qb.forUpdateSkipLocked();
        return qb.findList();
    }

    private QDelayedTask newDuePendingQuery(Instant now, int limit) {
        var qb = new QDelayedTask(db());
        qb.status.eq(Status.PENDING.getCode());
        qb.delayUntil.le(now);
        qb.setMaxRows(limit);
        return qb;
    }

    private QDelayedTask newExpiredProcessingQuery(Instant now, int limit) {
        var qb = new QDelayedTask(db());
        qb.status.eq(Status.PROCESSING.getCode());
        qb.raw(LEASE_EXPIRED_QUERY_PREDICATE, now);
        qb.lockAt.asc();
        qb.setMaxRows(limit);
        return qb;
    }

    /// 领取任务：仅当任务仍为 PENDING 且已到 delayUntil 才会成功。
    ///
    /// 返回 true 表示本次调用成功占有该任务；false 表示被其他工作节点先一步处理或尚未到期。
    public boolean claimPending(UUID id, Instant now, int leaseSeconds, int nextAttempts) {
        var qb = new QDelayedTask(db());
        qb.id.eq(id);
        qb.status.eq(Status.PENDING.getCode());
        qb.delayUntil.le(now);
        return qb.asUpdate()
                        .set(qb.status, Status.PROCESSING.getCode())
                        .set(qb.lockAt, now)
                        .set(qb.leaseSeconds, leaseSeconds)
                        .set(qb.attempts, nextAttempts)
                        .set(qb.updatedAt, Instant.now())
                        .update()
                == 1;
    }

    /// 完成任务：仅 PROCESSING -> COMPLETED。
    public void complete(UUID id) {
        updateTerminalStatus(id, Status.COMPLETED);
    }

    /// 归档任务：仅 PROCESSING -> ARCHIVED。
    public void archive(UUID id) {
        updateTerminalStatus(id, Status.ARCHIVED);
    }

    private void updateTerminalStatus(UUID id, Status terminalStatus) {
        updateTransition(newProcessingQuery(id), terminalStatus, null);
    }

    /// 将超时任务回退为待处理（PROCESSING -> PENDING）。
    ///
    /// 返回 true 表示成功迁移，false 表示不满足“处理中且租约超时”前置条件。
    public boolean transitionExpiredToPending(UUID id, Instant now, Instant delayUntil) {
        return updateTransition(newExpiredProcessingTransitionQuery(id, now), Status.PENDING, delayUntil);
    }

    /// 将超时任务置为失败（PROCESSING -> FAILED）。
    ///
    /// 返回 true 表示成功迁移，false 表示不满足“处理中且租约超时”前置条件。
    public boolean transitionExpiredToFailed(UUID id, Instant now) {
        return updateTransition(newExpiredProcessingTransitionQuery(id, now), Status.FAILED, null);
    }

    private QDelayedTask newProcessingQuery(UUID id) {
        var qb = new QDelayedTask(db());
        qb.id.eq(id);
        qb.status.eq(Status.PROCESSING.getCode());
        return qb;
    }

    private QDelayedTask newExpiredProcessingTransitionQuery(UUID id, Instant now) {
        var qb = newProcessingQuery(id);
        qb.raw(LEASE_EXPIRED_UPDATE_PREDICATE, now);
        return qb;
    }

    private boolean updateTransition(QDelayedTask qb, Status targetStatus, @Nullable Instant delayUntil) {
        return qb.asUpdate()
                        .set(qb.status, targetStatus.getCode())
                        .set(qb.lockAt, null)
                        .set(qb.delayUntil, delayUntil)
                        .set(qb.updatedAt, Instant.now())
                        .update()
                == 1;
    }

    private static class DelayedTaskHolder extends TransactionCallbackAdapter {

        private final DelayedTaskRepository repository;
        private final List<DelayedTask> tasks = new ArrayList<>(4);

        DelayedTaskHolder(DelayedTaskRepository repository) {
            this.repository = repository;
        }

        @Override
        public void preCommit() {
            if (!tasks.isEmpty()) {
                repository.saveAll(tasks);
                log.debug("批量持久化延时任务: {}", tasks);
            }
        }

        @Override
        public void preRollback() {
            if (!tasks.isEmpty()) {
                tasks.clear();
            }
        }

        DelayedTaskHolder add(DelayedTask message) {
            tasks.add(message);
            return this;
        }
    }
}

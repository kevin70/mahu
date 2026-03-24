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

    public PagedList<DelayedTask> findPage(@Nullable String topic, Page page) {
        var qb = new QDelayedTask(db());
        if (topic != null && !topic.isBlank()) {
            qb.topic.eq(topic);
        }
        return super.findPage(qb, page);
    }

    public List<DelayedTask> findDuePending(Instant now, int limit) {
        var qb = new QDelayedTask(db());
        qb.status.eq(Status.PENDING.getCode());
        qb.delayUntil.le(now);
        qb.setMaxRows(limit);
        return qb.findList();
    }

    public List<DelayedTask> findExpiredProcessing(Instant now, int limit) {
        var qb = new QDelayedTask(db());
        qb.status.eq(Status.PROCESSING.getCode());
        qb.raw(LEASE_EXPIRED_QUERY_PREDICATE, now);
        qb.lockAt.asc();
        qb.setMaxRows(limit);
        return qb.findList();
    }

    public List<DelayedTask> findDuePendingSkipLocked(Instant now, int limit) {
        var qb = new QDelayedTask(db());
        qb.status.eq(Status.PENDING.getCode());
        qb.delayUntil.le(now);
        qb.delayUntil.asc();
        qb.setMaxRows(limit);
        qb.forUpdateSkipLocked();
        return qb.findList();
    }

    public List<DelayedTask> findExpiredProcessingSkipLocked(Instant now, int limit) {
        var qb = new QDelayedTask(db());
        qb.status.eq(Status.PROCESSING.getCode());
        qb.raw(LEASE_EXPIRED_QUERY_PREDICATE, now);
        qb.lockAt.asc();
        qb.setMaxRows(limit);
        qb.forUpdateSkipLocked();
        return qb.findList();
    }

    public boolean claimPending(UUID id, Instant now, int leaseSeconds, int nextAttempts) {
        var qb = new QDelayedTask(db());
        return qb.id.eq(id)
                        .status
                        .eq(Status.PENDING.getCode())
                        .delayUntil
                        .le(now)
                        .asUpdate()
                        .set(qb.status, Status.PROCESSING.getCode())
                        .set(qb.lockAt, now)
                        .set(qb.leaseSeconds, leaseSeconds)
                        .set(qb.attempts, nextAttempts)
                        .set(qb.updatedAt, Instant.now())
                        .update()
                == 1;
    }

    public void complete(UUID id) {
        updateTerminalStatus(id, Status.COMPLETED, Status.PROCESSING.getCode());
    }

    public void archive(UUID id) {
        updateTerminalStatus(id, Status.ARCHIVED, Status.PROCESSING.getCode());
    }

    private void updateTerminalStatus(UUID id, Status terminalStatus, int expectedStatus) {
        var qb = new QDelayedTask(db());
        qb.id.eq(id)
                .status
                .eq(expectedStatus)
                .asUpdate()
                .set(qb.status, terminalStatus.getCode())
                .set(qb.delayUntil, null)
                .set(qb.lockAt, null)
                .set(qb.updatedAt, Instant.now())
                .update();
    }

    public boolean transitionExpiredToPending(UUID id, Instant now, Instant delayUntil) {
        var qb = new QDelayedTask(db());
        return qb.id.eq(id)
                        .status
                        .eq(Status.PROCESSING.getCode())
                        .raw(LEASE_EXPIRED_UPDATE_PREDICATE, now)
                        .asUpdate()
                        .set(qb.status, Status.PENDING.getCode())
                        .set(qb.lockAt, null)
                        .set(qb.delayUntil, delayUntil)
                        .set(qb.updatedAt, Instant.now())
                        .update()
                == 1;
    }

    public boolean transitionExpiredToFailed(UUID id, Instant now) {
        var qb = new QDelayedTask(db());
        return qb.id.eq(id)
                        .status
                        .eq(Status.PROCESSING.getCode())
                        .raw(LEASE_EXPIRED_UPDATE_PREDICATE, now)
                        .asUpdate()
                        .set(qb.status, Status.FAILED.getCode())
                        .set(qb.lockAt, null)
                        .set(qb.delayUntil, null)
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

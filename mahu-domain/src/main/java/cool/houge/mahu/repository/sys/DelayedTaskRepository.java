package cool.houge.mahu.repository.sys;

import com.github.f4b6a3.ulid.UlidFactory;
import cool.houge.mahu.Status;
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

    /// 入队（持久化）延时任务。
    ///
    /// - 若存在当前事务上下文：任务会先暂存，直到事务提交时批量落库
    /// - 若不存在事务上下文：任务会立即保存
    ///
    /// 说明：这里会自动生成 `DelayedTask.id`；`topic`/`payload`/`idempotencyKey`/`referenceId` 由调用方保证一致性；`referenceId` 必填且非空白。
    ///
    /// @param task 延时任务实体（id 会被覆盖重写）
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

    /// 分页查询延时任务（可按 topic 过滤）。
    ///
    /// @param page 分页参数
    /// @return 分页结果
    public PagedList<DelayedTask> findPage(@Nullable String topic, Page page) {
        var qb = new QDelayedTask(db());
        if (topic != null && !topic.isBlank()) {
            qb.topic.eq(topic);
        }
        return super.findPage(qb, page);
    }

    /// 查找到点可领取的任务：`status=PENDING && delayUntil<=now`（全 topic）。
    public List<DelayedTask> findDuePending(Instant now, int limit) {
        var qb = new QDelayedTask(db());
        qb.status.eq(Status.PENDING.getCode());
        qb.delayUntil.le(now);
        qb.setMaxRows(limit);
        return qb.findList();
    }

    /// 查找租约到期仍处于 PROCESSING 的任务（全 topic）：`status=PROCESSING` 且 `lock_at <= now - lease`。
    public List<DelayedTask> findExpiredProcessing(Instant now, int limit) {
        var qb = new QDelayedTask(db());
        qb.status.eq(Status.PROCESSING.getCode());
        qb.raw(LEASE_EXPIRED_QUERY_PREDICATE, now);
        qb.lockAt.asc();
        qb.setMaxRows(limit);
        return qb.findList();
    }

    /// 查找到点可领取的任务（`status=PENDING && delayUntil<=now`），并加 `FOR UPDATE SKIP LOCKED`。
    ///
    /// <p>仅查询、不修改状态；须在调用方同一事务内配合 `claimPending` 或等价更新完成领取。</p>
    public List<DelayedTask> findDuePendingSkipLocked(Instant now, int limit) {
        var qb = new QDelayedTask(db());
        qb.status.eq(Status.PENDING.getCode());
        qb.delayUntil.le(now);
        qb.delayUntil.asc();
        qb.setMaxRows(limit);
        qb.forUpdateSkipLocked();
        return qb.findList();
    }

    /// 查找租约到期仍处于 PROCESSING 的任务，并加 `FOR UPDATE SKIP LOCKED`。
    ///
    /// <p>仅查询、不修改状态；须在调用方同一事务内配合 `transitionExpiredToPending` / `transitionExpiredToFailed` 完成回收。</p>
    public List<DelayedTask> findExpiredProcessingSkipLocked(Instant now, int limit) {
        var qb = new QDelayedTask(db());
        qb.status.eq(Status.PROCESSING.getCode());
        qb.raw(LEASE_EXPIRED_QUERY_PREDICATE, now);
        qb.lockAt.asc();
        qb.setMaxRows(limit);
        qb.forUpdateSkipLocked();
        return qb.findList();
    }

    /// 原子领取任务：仅当任务满足 `status=PENDING && delayUntil<=now` 才能将其置为 `PROCESSING`。
    ///
    /// 同时写入：
    /// - `lockAt=now`
    /// - `leaseSeconds=leaseSeconds`
    /// - `attempts=nextAttempts`
    ///
    /// 返回值表示是否成功更新到一行（失败通常意味着已被其它 worker claim）。
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

    /// 标记为已完成（终结态）：仅允许从 `PROCESSING` 切换到 `COMPLETED`。
    ///
    /// 通过条件更新保证幂等/并发安全：当 status 已不为 PROCESSING 时本次更新无效。
    public void complete(UUID id) {
        // 幂等/并发安全：只有仍处于 PROCESSING 的任务，才允许切到终结态。
        updateTerminalStatus(id, Status.COMPLETED, Status.PROCESSING.getCode());
    }

    /// 标记为归档（终结态）：仅允许从 `PROCESSING` 切换到 `ARCHIVED`。
    ///
    /// 通过条件更新保证幂等/并发安全：当 status 已不为 PROCESSING 时本次更新无效。
    public void archive(UUID id) {
        // 幂等/并发安全：只有仍处于 PROCESSING 的任务，才允许切到终结态。
        updateTerminalStatus(id, Status.ARCHIVED, Status.PROCESSING.getCode());
    }

    private void updateTerminalStatus(UUID id, Status terminalStatus, int expectedStatus) {
        var qb = new QDelayedTask(db());
        // 仅当 status==expectedStatus 时才更新，避免 worker 超时回收期间被其它状态转换覆盖。
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

    /// 超时回收：将「租约已到期」的 `PROCESSING` 任务转为 `PENDING`（重试）。
    ///
    /// 不修改 attempts：下一次 claimPending 会以 `attempts+1` 的方式推进重试次数。
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

    /// 超时回收：将「租约已到期」的 `PROCESSING` 任务转为 `FAILED`（不可再重试）。
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

    /// 延时任务持有者，用于在事务中暂存任务
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

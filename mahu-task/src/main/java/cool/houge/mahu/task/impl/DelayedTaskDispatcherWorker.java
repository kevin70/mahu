package cool.houge.mahu.task.impl;

import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.repository.sys.DelayedTaskRepository;
import cool.houge.mahu.task.handler.ClaimedDelayedTask;
import cool.houge.mahu.task.handler.DelayedTaskCompletionResult;
import cool.houge.mahu.task.handler.DelayedTaskHandler;
import io.ebean.annotation.Transactional;
import io.helidon.config.Config;
import io.helidon.scheduling.FixedRate;
import io.helidon.service.registry.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 延时任务调度 worker：
/// - 轮询获取 delayed_tasks 中到点/租约到期任务
/// - claim 后按 topic 路由到对应的 DI handler
/// - handler 只做业务逻辑判定/更新；delayed_task 状态落库由 worker 统一完成
@Service.Singleton
@AllArgsConstructor
public class DelayedTaskDispatcherWorker {

    private static final Logger log = LogManager.getLogger(DelayedTaskDispatcherWorker.class);
    private static final String SCHEDULING_KEY = "scheduling.delayed-task-dispatcher";
    private static final Duration DEFAULT_INTERVAL = Duration.ofSeconds(5);
    private static final int BATCH_SIZE = 50;

    private final Config config;
    private final List<DelayedTaskHandler> delayedTaskHandlers;
    private final DelayedTaskRepository delayedTaskRepository;

    @Service.PostConstruct
    void init() {
        FixedRate.builder()
                .interval(DEFAULT_INTERVAL)
                .delayBy(DEFAULT_INTERVAL)
                .config(config.get(SCHEDULING_KEY))
                .task(inv -> safeExecute())
                .build();
    }

    private void safeExecute() {
        try {
            execute();
        } catch (Exception e) {
            // 避免调度线程因未捕获异常中断，错误留痕后等待下次调度恢复。
            log.error("延时任务调度器(pending)：本轮执行失败", e);
        }
    }

    /// `findDuePendingSkipLocked` + `claimPending` 必须在同一事务内，行锁语义才成立；
    /// claim 完成后才在事务外执行 topic handler，并由 Worker 统一完成状态更新。
    void execute() {
        var claimedTasks = claimDuePending();
        if (claimedTasks.isEmpty()) {
            return;
        }

        var completed = 0;
        for (ClaimedDelayedTask task : claimedTasks) {
            if (dispatchOne(task)) {
                completed++;
            }
        }

        log.info(
                "延时任务调度器(pending)：本次领取/处理 claimed={}, completed={}, at={}",
                claimedTasks.size(),
                completed,
                Instant.now());
    }

    private boolean dispatchOne(ClaimedDelayedTask task) {
        try {
            var handler = resolveHandler(task.topic());
            if (handler == null) {
                log.warn(
                        "延时任务调度器(pending)：未找到 topic 处理器 topic={}，将归档 delayedTaskId={}",
                        task.topic(),
                        task.delayedTaskId());
                delayedTaskRepository.archive(task.delayedTaskId());
                return true;
            }

            finishTask(task.delayedTaskId(), handler.handle(task));
            return true;
        } catch (Exception e) {
            // 失败时保持 PROCESSING，由租约回收 worker 基于 attempts/maxAttempts 重试或停止
            log.error(
                    "延时任务调度器(pending)：处理器执行失败，将等待租约回收重试 delayedTaskId={}, topic={}",
                    task.delayedTaskId(),
                    task.topic(),
                    e);
            return false;
        }
    }

    private void finishTask(UUID delayedTaskId, DelayedTaskCompletionResult action) {
        switch (action) {
            case COMPLETE -> delayedTaskRepository.complete(delayedTaskId);
            case ARCHIVE -> delayedTaskRepository.archive(delayedTaskId);
        }
    }

    @Transactional
    private List<ClaimedDelayedTask> claimDuePending() {
        var claimedTasks = new ArrayList<ClaimedDelayedTask>(BATCH_SIZE);
        var now = Instant.now();
        var candidates = delayedTaskRepository.findDuePendingSkipLocked(now, BATCH_SIZE);
        for (DelayedTask task : candidates) {
            if (claimPendingToProcessing(task, now)) {
                claimedTasks.add(new ClaimedDelayedTask(
                        task.getId(),
                        task.getTopic(),
                        task.getReferenceId(),
                        task.getPayload(),
                        task.getIdempotencyKey(),
                        task.getDelayUntil()));
            }
        }
        return List.copyOf(claimedTasks);
    }

    private DelayedTaskHandler resolveHandler(String topic) {
        return delayedTaskHandlers.stream()
                .filter(handler -> handler.supports(topic))
                .findFirst()
                .orElse(null);
    }

    private boolean claimPendingToProcessing(DelayedTask task, Instant now) {
        var attempts = task.getAttempts() == null ? 0 : task.getAttempts();
        var nextAttempts = attempts + 1;
        var leaseSeconds =
                task.getLeaseSeconds() == null ? DelayedTaskRepository.DEFAULT_LEASE_SECONDS : task.getLeaseSeconds();
        return delayedTaskRepository.claimPending(task.getId(), now, leaseSeconds, nextAttempts);
    }
}

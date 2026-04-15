package cool.houge.mahu.task.impl;

import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.repository.sys.DelayedTaskRepository;
import cool.houge.mahu.task.handler.ClaimedDelayedTask;
import cool.houge.mahu.task.handler.DelayedTaskCompletionResult;
import cool.houge.mahu.task.handler.DelayedTaskHandler;
import io.ebean.annotation.Transactional;
import io.helidon.config.Config;
import io.helidon.scheduling.FixedRate;
import io.helidon.scheduling.TaskManager;
import io.helidon.service.registry.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 延时任务分发 worker：
/// - 扫描已到期的 PENDING 任务
/// - 领取后按 topic 分发到对应 handler
/// - 任务终态由 worker 统一落库
@Service.Singleton
@Service.RunLevel(Service.RunLevel.SERVER)
@AllArgsConstructor
public class DelayedTaskDispatcherWorker {

    private static final Logger log = LogManager.getLogger(DelayedTaskDispatcherWorker.class);
    private static final String SCHEDULING_KEY = "scheduling.delayed-task-dispatcher";
    private static final Duration DEFAULT_INTERVAL = Duration.ofSeconds(5);
    private static final int BATCH_SIZE = 50;

    private final Config config;
    private final TaskManager taskManager;
    private final List<DelayedTaskHandler> delayedTaskHandlers;
    private final DelayedTaskRepository delayedTaskRepository;

    @Service.PostConstruct
    void init() {
        var task = FixedRate.builder()
                .interval(DEFAULT_INTERVAL)
                .delayBy(DEFAULT_INTERVAL)
                .config(config.get(SCHEDULING_KEY))
                .task(inv -> execute())
                .build();
        taskManager.register(task);
    }

    /// 调度入口。
    ///
    /// 统一在此处兜底异常，避免调度线程被未捕获异常中断。
    void execute() {
        try {
            executeAt(Instant.now());
        } catch (Exception e) {
            // 避免调度线程因未捕获异常中断，错误留痕后等待下次调度恢复。
            log.error("延时任务调度器(pending)：本轮执行失败", e);
        }
    }

    @Transactional
    void executeAt(Instant now) {
        var claimedTasks = this.claimDuePending(now);
        if (claimedTasks.isEmpty()) {
            return;
        }

        var completed = processClaimedTasks(claimedTasks);

        log.info("延时任务调度器(pending)：本次领取/处理 claimed={}, completed={}, at={}", claimedTasks.size(), completed, now);
    }

    private int processClaimedTasks(List<ClaimedDelayedTask> claimedTasks) {
        var completed = 0;
        for (ClaimedDelayedTask task : claimedTasks) {
            if (dispatchOne(task)) {
                completed++;
            }
        }
        return completed;
    }

    private boolean dispatchOne(ClaimedDelayedTask task) {
        try {
            var handler = resolveHandler(task.getTopic());
            if (handler == null) {
                log.warn(
                        "延时任务调度器(pending)：未找到 topic 处理器 topic={}，将归档 delayedTaskId={}",
                        task.getTopic(),
                        task.getDelayedTaskId());
                delayedTaskRepository.archive(task.getDelayedTaskId());
                return true;
            }

            finishTask(task.getDelayedTaskId(), handler.handle(task));
            return true;
        } catch (Exception e) {
            // 失败时保持 PROCESSING，由超时回收 worker 决定重试或失败。
            log.error(
                    "延时任务调度器(pending)：处理器执行失败，将等待租约回收重试 delayedTaskId={}, topic={}",
                    task.getDelayedTaskId(),
                    task.getTopic(),
                    e);
            return false;
        }
    }

    private void finishTask(UUID delayedTaskId, DelayedTaskCompletionResult action) {
        if (action == DelayedTaskCompletionResult.COMPLETE) {
            delayedTaskRepository.complete(delayedTaskId);
        } else {
            delayedTaskRepository.archive(delayedTaskId);
        }
    }

    private List<ClaimedDelayedTask> claimDuePending(Instant now) {
        var claimedTasks = new ArrayList<ClaimedDelayedTask>(BATCH_SIZE);
        var candidates = delayedTaskRepository.findDuePendingSkipLocked(now, BATCH_SIZE);
        for (DelayedTask task : candidates) {
            if (claimPendingToProcessing(task, now)) {
                claimedTasks.add(ClaimedDelayedTask.builder()
                        .delayedTaskId(task.getId())
                        .topic(task.getTopic())
                        .referenceId(task.getReferenceId())
                        .payload(task.getPayload())
                        .idempotencyKey(task.getIdempotencyKey())
                        .delayUntil(task.getDelayUntil())
                        .build());
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

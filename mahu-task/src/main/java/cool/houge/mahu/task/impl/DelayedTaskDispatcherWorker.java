package cool.houge.mahu.task.impl;

import static com.github.kagkarlsson.scheduler.task.schedule.Schedules.fixedDelay;

import com.github.kagkarlsson.scheduler.task.ExecutionContext;
import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.repository.sys.DelayedTaskRepository;
import cool.houge.mahu.shared.service.AppSharedService;
import cool.houge.mahu.task.handler.ClaimedDelayedTask;
import cool.houge.mahu.task.handler.DelayedTaskCompletionResult;
import cool.houge.mahu.task.handler.DelayedTaskHandler;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 延时任务调度 worker：
/// - 轮询获取 delayed_tasks 中到点/租约到期任务
/// - claim 后按 topic 路由到对应的 DI handler
/// - handler 只做业务逻辑判定/更新；delayed_task 状态落库由 worker 统一完成
@Service.Singleton
@AllArgsConstructor
public class DelayedTaskDispatcherWorker implements Supplier<Task<?>> {

    private static final Logger log = LogManager.getLogger(DelayedTaskDispatcherWorker.class);
    private static final int BATCH_SIZE = 50;
    private static final int MAX_BATCHES = 3;

    private final List<DelayedTaskHandler> delayedTaskHandlers;
    private final DelayedTaskRepository delayedTaskRepository;
    private final AppSharedService appSharedService;

    @Override
    public Task<?> get() {
        return Tasks.recurring("delayed-tasks-dispatcher-pending", fixedDelay(Duration.ofSeconds(5)))
                .execute(this::execute);
    }

    /// `findDuePendingSkipLocked` + `claimPending` 必须在同一事务内，行锁语义才成立；
    /// claim 完成后才在事务外执行 topic handler，并由 Worker 统一完成状态更新。
    void execute(TaskInstance<Void> taskInstance, ExecutionContext context) {
        ClaimResult claimResult = claimDuePending(MAX_BATCHES);
        if (claimResult.tasks.isEmpty()) {
            return;
        }

        var completed = 0;
        for (ClaimedDelayedTask task : claimResult.tasks) {
            try {
                var handler = resolveHandler(task.topic());
                if (handler == null) {
                    log.warn(
                            "延时任务调度器(pending)：未找到 topic 处理器 topic={}，将归档 delayedTaskId={}",
                            task.topic(),
                            task.delayedTaskId());
                    appSharedService.archiveDelayedTask(task.delayedTaskId());
                    completed++;
                    continue;
                }

                DelayedTaskCompletionResult action = handler.handle(task);
                switch (action) {
                    case COMPLETE -> appSharedService.completeDelayedTask(task.delayedTaskId());
                    case ARCHIVE -> appSharedService.archiveDelayedTask(task.delayedTaskId());
                }
                completed++;
            } catch (Exception e) {
                // 失败时保持 PROCESSING，由租约回收 worker 基于 attempts/maxAttempts 重试或停止
                log.error(
                        "延时任务调度器(pending)：处理器执行失败，将等待租约回收重试 delayedTaskId={}, topic={}",
                        task.delayedTaskId(),
                        task.topic(),
                        e);
            }
        }

        log.info(
                "延时任务调度器(pending)：本次领取/处理 claimed={}, completed={}, 批次数 batchesProcessed={}, at={}",
                claimResult.tasks.size(),
                completed,
                claimResult.batchesProcessed,
                Instant.now());
    }

    @Transactional
    private ClaimResult claimDuePending(int maxBatches) {
        var claimedTasks = new ArrayList<ClaimedDelayedTask>(BATCH_SIZE * maxBatches);
        var batchesProcessed = 0;

        for (var batch = 0; batch < maxBatches; batch++) {
            var now = Instant.now();
            var candidates = delayedTaskRepository.findDuePendingSkipLocked(now, BATCH_SIZE);
            if (candidates.isEmpty()) {
                break;
            }

            batchesProcessed++;
            for (DelayedTask task : candidates) {
                if (claimPendingToProcessing(task, now)) {
                    claimedTasks.add(new ClaimedDelayedTask(
                            task.getId(),
                            task.getTopic(),
                        task.getReferenceId(), task.getPayload(),
                        task.getDelayUntil()));
                }
            }

            if (candidates.size() < BATCH_SIZE) {
                break;
            }
        }

        return new ClaimResult(List.copyOf(claimedTasks), batchesProcessed);
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

    private record ClaimResult(List<ClaimedDelayedTask> tasks, int batchesProcessed) {}
}

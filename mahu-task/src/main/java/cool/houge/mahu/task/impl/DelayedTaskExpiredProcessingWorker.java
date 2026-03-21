package cool.houge.mahu.task.impl;

import static com.github.kagkarlsson.scheduler.task.schedule.Schedules.fixedDelay;

import com.github.kagkarlsson.scheduler.task.ExecutionContext;
import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.repository.sys.DelayedTaskRepository;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 延时任务超时租约处理 worker：
/// - 查找租约到期仍处于 PROCESSING 的 delayed_tasks
/// - 根据 attempts/maxAttempts 转换为 PENDING(重试) 或 FAILED(停止重试)
@Service.Singleton
@AllArgsConstructor
public class DelayedTaskExpiredProcessingWorker implements Supplier<Task<?>> {

    private static final Logger log = LogManager.getLogger(DelayedTaskExpiredProcessingWorker.class);
    private static final int BATCH_SIZE = 50;

    private final DelayedTaskRepository delayedTaskRepository;

    @Override
    public Task<?> get() {
        return Tasks.recurring("delayed-tasks-dispatcher-expired-processing", fixedDelay(Duration.ofSeconds(5)))
                .execute(this::execute);
    }

    /// `findExpiredProcessingSkipLocked` + 状态转换必须在同一事务内，行锁语义才成立。
    @Transactional
    private Void execute(TaskInstance<Void> taskInstance, ExecutionContext context) {
        var now = Instant.now();
        var candidates = delayedTaskRepository.findExpiredProcessingSkipLocked(now, BATCH_SIZE);
        var transitioned = 0;
        for (DelayedTask task : candidates) {
            if (transitionExpiredToPendingOrFailed(task, now)) {
                transitioned++;
            }
        }
        if (transitioned > 0) {
            log.info(
                    "DelayedTaskExpiredProcessingWorker(expired): transitioned(expired->pending/failed)={}, at={}",
                    transitioned,
                    now);
        }
        return null;
    }

    private boolean transitionExpiredToPendingOrFailed(DelayedTask task, Instant now) {
        var attempts = task.getAttempts() == null ? 0 : task.getAttempts();
        var maxAttempts = task.getMaxAttempts() == null ? 1 : task.getMaxAttempts();
        if (attempts >= maxAttempts) {
            return delayedTaskRepository.transitionExpiredToFailed(task.getId(), now);
        }
        var delayUntil = computeRetryAt(now, attempts);
        return delayedTaskRepository.transitionExpiredToPending(task.getId(), now, delayUntil);
    }

    private static Instant computeRetryAt(Instant now, int attempts) {
        var backoffSeconds = Math.min(3600, attempts * 30L);
        return now.plusSeconds(backoffSeconds);
    }
}

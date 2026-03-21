package cool.houge.mahu.task.impl;

import static com.github.kagkarlsson.scheduler.task.schedule.Schedules.fixedDelay;

import com.github.kagkarlsson.scheduler.task.ExecutionContext;
import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.repository.sys.DelayedTaskRepository;
import cool.houge.mahu.task.event.DelayedTaskClaimedEvent;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Event;
import io.helidon.service.registry.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 延时任务调度 worker：
/// - 轮询获取 delayed_tasks 中到点/租约到期任务
/// - 根据 topic 进行 claim 后发布 Helidon Event
/// - 具体业务处理由 @Observer 监听器完成（避免 worker 里耦合业务）
@Service.Singleton
@AllArgsConstructor
public class DelayedTaskDispatcherWorker implements Supplier<Task<?>> {

    private static final Logger log = LogManager.getLogger(DelayedTaskDispatcherWorker.class);
    private static final int BATCH_SIZE = 50;

    private final DelayedTaskRepository delayedTaskRepository;
    private final Event.Emitter<DelayedTaskClaimedEvent> delayedTaskClaimedEmitter;

    @Override
    public Task<?> get() {
        return Tasks.recurring("delayed-tasks-dispatcher-pending", fixedDelay(Duration.ofSeconds(5)))
                .execute(this::execute);
    }

    /// `findDuePendingSkipLocked` + `claimPending` + emit 必须在同一事务内，行锁语义才成立。
    @Transactional
    void execute(TaskInstance<Void> taskInstance, ExecutionContext context) {
        var now = Instant.now();
        var candidates = delayedTaskRepository.findDuePendingSkipLocked(now, BATCH_SIZE);
        var emitted = 0;
        for (DelayedTask task : candidates) {
            if (claimPendingToProcessing(task, now)) {
                delayedTaskClaimedEmitter.emit(new DelayedTaskClaimedEvent(
                        task.getId(), task.getTopic(), task.getPayload(), task.getReferenceId()));
                emitted++;
            }
        }
        if (emitted > 0) {
            log.info("DelayedTaskDispatcherWorker(pending): emitted={}, at={}", emitted, now);
        }
    }

    private boolean claimPendingToProcessing(DelayedTask task, Instant now) {
        var attempts = task.getAttempts() == null ? 0 : task.getAttempts();
        var nextAttempts = attempts + 1;
        var leaseSeconds =
                task.getLeaseSeconds() == null ? DelayedTaskRepository.DEFAULT_LEASE_SECONDS : task.getLeaseSeconds();
        return delayedTaskRepository.claimPending(task.getId(), now, leaseSeconds, nextAttempts);
    }
}

package cool.houge.mahu.task.impl;

import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.repository.sys.DelayedTaskRepository;
import io.ebean.annotation.Transactional;
import io.helidon.config.Config;
import io.helidon.scheduling.FixedRate;
import io.helidon.service.registry.Service;
import java.time.Duration;
import java.time.Instant;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 延时任务超时租约处理 worker：
/// - 查找租约到期仍处于 PROCESSING 的 delayed_tasks
/// - 根据 attempts/maxAttempts 转换为 PENDING(重试) 或 FAILED(停止重试)
@Service.Singleton
@AllArgsConstructor
public class DelayedTaskExpiredProcessingWorker {

    private static final Logger log = LogManager.getLogger(DelayedTaskExpiredProcessingWorker.class);
    private static final String SCHEDULING_KEY = "scheduling.delayed-task-expired-processing";
    private static final Duration DEFAULT_INTERVAL = Duration.ofSeconds(5);
    private static final int BATCH_SIZE = 50;

    private final Config config;
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
            log.error("延时任务调度器(expired-processing)：本轮执行失败", e);
        }
    }

    /// `findExpiredProcessingSkipLocked` + 状态转换必须在同一事务内，行锁语义才成立。
    @Transactional
    void execute() {
        executeAt(Instant.now());
    }

    void executeAt(Instant now) {
        var candidates = delayedTaskRepository.findExpiredProcessingSkipLocked(now, BATCH_SIZE);
        var transitioned = 0;
        for (DelayedTask task : candidates) {
            if (transitionExpiredToPendingOrFailed(task, now)) {
                transitioned++;
            }
        }
        if (transitioned > 0) {
            log.info(
                    "延时任务调度器(expired-processing)：本次回收 transitioned(expired->pending/failed)={}, at={}",
                    transitioned,
                    now);
        }
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

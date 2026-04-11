package cool.houge.mahu.task.impl;

import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.repository.sys.DelayedTaskRepository;
import io.ebean.annotation.Transactional;
import io.helidon.config.Config;
import io.helidon.scheduling.FixedRate;
import io.helidon.scheduling.TaskManager;
import io.helidon.service.registry.Service;
import java.time.Duration;
import java.time.Instant;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 延时任务超时回收 worker：
/// - 查找租约到期仍处于 PROCESSING 的任务
/// - 根据 attempts/maxAttempts 转换为重试或失败
@Service.Singleton
@Service.RunLevel(Service.RunLevel.SERVER)
@AllArgsConstructor
public class DelayedTaskExpiredProcessingWorker {

    private static final Logger log = LogManager.getLogger(DelayedTaskExpiredProcessingWorker.class);
    private static final String SCHEDULING_KEY = "scheduling.delayed-task-expired-processing";
    private static final Duration DEFAULT_INTERVAL = Duration.ofSeconds(5);
    private static final int BATCH_SIZE = 50;

    private final Config config;
    private final TaskManager taskManager;
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
            log.error("延时任务调度器(expired-processing)：本轮执行失败", e);
        }
    }

    @Transactional
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

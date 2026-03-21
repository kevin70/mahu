package cool.houge.mahu.task.handler.impl;

import cool.houge.mahu.delayed.DelayedTaskTopics;
import cool.houge.mahu.repository.sys.FeatureFlagRepository;
import cool.houge.mahu.task.handler.ClaimedDelayedTask;
import cool.houge.mahu.task.handler.DelayedTaskCompletionResult;
import cool.houge.mahu.task.handler.DelayedTaskHandler;
import io.helidon.service.registry.Service.Singleton;
import java.time.Instant;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 处理 sys.delayed_tasks 中与功能开关翻转相关的延时任务。
@Singleton
@AllArgsConstructor
public class FeatureFlagDelayedTaskHandler implements DelayedTaskHandler {

    private static final Logger log = LogManager.getLogger(FeatureFlagDelayedTaskHandler.class);

    private static final String ENABLE_TOPIC = DelayedTaskTopics.FEATURE_FLAG_ENABLE.topic();
    private static final String DISABLE_TOPIC = DelayedTaskTopics.FEATURE_FLAG_DISABLE.topic();

    private final FeatureFlagRepository featureFlagRepository;

    @Override
    public boolean supports(String topic) {
        return ENABLE_TOPIC.equals(topic) || DISABLE_TOPIC.equals(topic);
    }

    @Override
    public DelayedTaskCompletionResult handle(ClaimedDelayedTask task) {
        final int featureFlagId;
        try {
            featureFlagId = Integer.parseInt(task.referenceId());
        } catch (Exception e) {
            log.warn(
                    "功能开关延时任务：referenceId 非法，归档 delayedTaskId={}, referenceId={}",
                    task.delayedTaskId(),
                    task.referenceId(),
                    e);
            return DelayedTaskCompletionResult.ARCHIVE;
        }

        var now = Instant.now();
        if (ENABLE_TOPIC.equals(task.topic())) {
            featureFlagRepository.enableIfDue(featureFlagId, now);
        }
        if (DISABLE_TOPIC.equals(task.topic())) {
            featureFlagRepository.disableIfDue(featureFlagId, now);
        }
        return DelayedTaskCompletionResult.COMPLETE;
    }
}

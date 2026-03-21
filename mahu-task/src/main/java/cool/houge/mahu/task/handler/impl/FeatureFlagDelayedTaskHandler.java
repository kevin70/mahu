package cool.houge.mahu.task.handler.impl;

import cool.houge.mahu.delayed.DelayedTaskTopics;
import cool.houge.mahu.entity.sys.FeatureFlag;
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
        var featureFlag = featureFlagRepository.findById(Integer.valueOf(task.referenceId()));
        if (featureFlag == null) {
            return DelayedTaskCompletionResult.ARCHIVE;
        }

        var now = Instant.now();
        if (ENABLE_TOPIC.equals(task.topic())) {
            applyEnableIfExpected(featureFlag, now);
        }
        if (DISABLE_TOPIC.equals(task.topic())) {
            applyDisableIfExpected(featureFlag, now);
        }
        return DelayedTaskCompletionResult.COMPLETE;
    }

    private void applyEnableIfExpected(FeatureFlag featureFlag, Instant now) {
        var enableAt = featureFlag.getEnableAt();
        if (enableAt == null || enableAt.isAfter(now)) {
            return;
        }
        featureFlag.setEnabled(true);
        featureFlag.setEnableAt(null);
        featureFlagRepository.update(featureFlag);
    }

    private void applyDisableIfExpected(FeatureFlag featureFlag, Instant now) {
        var disableAt = featureFlag.getDisableAt();
        if (disableAt == null || disableAt.isAfter(now)) {
            return;
        }
        featureFlag.setEnabled(false);
        featureFlag.setDisableAt(null);
        featureFlagRepository.update(featureFlag);
    }
}

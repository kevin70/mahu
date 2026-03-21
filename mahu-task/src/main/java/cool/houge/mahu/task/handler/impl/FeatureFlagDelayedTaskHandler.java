package cool.houge.mahu.task.handler.impl;

import cool.houge.mahu.delayed.DelayedTaskTopics;
import cool.houge.mahu.entity.sys.FeatureFlag;
import cool.houge.mahu.repository.sys.FeatureFlagRepository;
import cool.houge.mahu.task.handler.ClaimedDelayedTask;
import cool.houge.mahu.task.handler.DelayedTaskCompletionResult;
import cool.houge.mahu.task.handler.DelayedTaskHandler;
import io.helidon.service.registry.Service.Singleton;
import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 处理 sys.delayed_tasks 中与功能开关翻转相关的延时任务。
@Singleton
public class FeatureFlagDelayedTaskHandler implements DelayedTaskHandler {

    private static final Logger log = LogManager.getLogger(FeatureFlagDelayedTaskHandler.class);

    private static final String ENABLE_TOPIC = DelayedTaskTopics.FEATURE_FLAG_ENABLE.topic();
    private static final String DISABLE_TOPIC = DelayedTaskTopics.FEATURE_FLAG_DISABLE.topic();

    private final FeatureFlagRepository featureFlagRepository;

    public FeatureFlagDelayedTaskHandler(FeatureFlagRepository featureFlagRepository) {
        this.featureFlagRepository = featureFlagRepository;
    }

    @Override
    public boolean supports(String topic) {
        return ENABLE_TOPIC.equals(topic) || DISABLE_TOPIC.equals(topic);
    }

    @Override
    public DelayedTaskCompletionResult handle(ClaimedDelayedTask task, Instant now) {
        // 不使用 payload：referenceId 即 featureFlag 的数据 ID（见 FeatureFlagService/enqueue）。
        Integer featureFlagId;
        try {
            featureFlagId = task.referenceId() == null ? null : Integer.parseInt(task.referenceId());
        } catch (Exception e) {
            log.warn(
                    "功能开关延时任务：referenceId 非法，将直接完成 delayedTaskId={}, referenceId={}",
                    task.delayedTaskId(),
                    task.referenceId(),
                    e);
            return DelayedTaskCompletionResult.COMPLETE;
        }

        if (featureFlagId == null) {
            return DelayedTaskCompletionResult.COMPLETE;
        }

        FeatureFlag featureFlag = featureFlagRepository.findById(featureFlagId);
        if (featureFlag == null) {
            return DelayedTaskCompletionResult.COMPLETE;
        }

        // 由于同一 featureFlag 可能会在任务到点前被重复更新，worker 需要做“期望时间”校验。
        // delayUntil = enqueue 时传入的 expectedAt（即 payload 中携带的 expectedAtEpochMilli 的来源）。
        var expectedAt = task.delayUntil();
        if (ENABLE_TOPIC.equals(task.topic())) {
            applyEnableIfExpected(featureFlag, expectedAt, now);
            return DelayedTaskCompletionResult.COMPLETE;
        }
        if (DISABLE_TOPIC.equals(task.topic())) {
            applyDisableIfExpected(featureFlag, expectedAt, now);
            return DelayedTaskCompletionResult.COMPLETE;
        }

        // 理论上不会走到这里（supports 已保证 topic 一致）
        return DelayedTaskCompletionResult.COMPLETE;
    }

    private void applyEnableIfExpected(FeatureFlag featureFlag, Instant expectedAt, Instant now) {
        var enableAt = featureFlag.getEnableAt();
        if (enableAt == null || enableAt.isAfter(now)) {
            return;
        }
        if (expectedAt != null && enableAt.toEpochMilli() != expectedAt.toEpochMilli()) {
            // 期望时间不匹配：认为这条延时任务已过期，做 no-op（但仍完成该 delayed_task）
            return;
        }
        featureFlag.setEnabled(true);
        featureFlag.setEnableAt(null);
        featureFlagRepository.update(featureFlag);
    }

    private void applyDisableIfExpected(FeatureFlag featureFlag, Instant expectedAt, Instant now) {
        var disableAt = featureFlag.getDisableAt();
        if (disableAt == null || disableAt.isAfter(now)) {
            return;
        }
        if (expectedAt != null && disableAt.toEpochMilli() != expectedAt.toEpochMilli()) {
            // 期望时间不匹配：认为这条延时任务已过期，做 no-op（但仍完成该 delayed_task）
            return;
        }
        featureFlag.setEnabled(false);
        featureFlag.setDisableAt(null);
        featureFlagRepository.update(featureFlag);
    }
}

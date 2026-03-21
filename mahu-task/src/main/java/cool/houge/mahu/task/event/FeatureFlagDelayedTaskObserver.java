package cool.houge.mahu.task.event;

import cool.houge.mahu.delayed.DelayedTaskTopics;
import cool.houge.mahu.entity.sys.FeatureFlag;
import cool.houge.mahu.shared.service.AppSharedService;
import cool.houge.mahu.repository.sys.FeatureFlagRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Event.Observer;
import io.helidon.service.registry.Service.Singleton;
import java.time.Instant;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 监听并处理功能开关延时任务事件。
@Singleton
@AllArgsConstructor
public class FeatureFlagDelayedTaskObserver {

    private static final Logger log = LogManager.getLogger(FeatureFlagDelayedTaskObserver.class);
    private static final String ENABLE_TOPIC = DelayedTaskTopics.FEATURE_FLAG_ENABLE.topic();
    private static final String DISABLE_TOPIC = DelayedTaskTopics.FEATURE_FLAG_DISABLE.topic();

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private final FeatureFlagRepository featureFlagRepository;
    private final AppSharedService appSharedService;

    private static boolean isSupportedTopic(String topic) {
        return ENABLE_TOPIC.equals(topic) || DISABLE_TOPIC.equals(topic);
    }

    @Observer
    @Transactional
    void onDelayedTaskClaimed(DelayedTaskClaimedEvent event) {
        if (event == null || !isSupportedTopic(event.topic())) {
            return;
        }
        handleClaimed(event);
    }

    private void handleClaimed(DelayedTaskClaimedEvent event) {
        var taskId = event.delayedTaskId();
        var now = Instant.now();

        var payload = parsePayload(event.payload());
        if (payload == null) {
            appSharedService.completeDelayedTask(taskId);
            return;
        }

        FeatureFlag featureFlag = featureFlagRepository.findById(payload.featureFlagId());
        if (featureFlag == null) {
            appSharedService.completeDelayedTask(taskId);
            return;
        }

        // 只要开启/关闭时间已到（<= 当前时间），才会翻转；否则不修改，但仍视为任务完成
        applyTopicAction(featureFlag, event.topic(), now);
        appSharedService.completeDelayedTask(taskId);
    }

    private void applyTopicAction(
            FeatureFlag featureFlag,
            String topic,
            Instant now
    ) {
        if (ENABLE_TOPIC.equals(topic)) {
            var enableAt = featureFlag.getEnableAt();
            if (enableAt != null && !enableAt.isAfter(now)) {
                featureFlag.setEnabled(true);
                featureFlag.setEnableAt(null);
                featureFlagRepository.update(featureFlag);
            }
            return;
        }
        if (DISABLE_TOPIC.equals(topic)) {
            var disableAt = featureFlag.getDisableAt();
            if (disableAt != null && !disableAt.isAfter(now)) {
                featureFlag.setEnabled(false);
                featureFlag.setDisableAt(null);
                featureFlagRepository.update(featureFlag);
            }
        }
    }

    private static FeatureFlagPayload parsePayload(String payload) {
        if (payload == null || payload.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readValue(payload, FeatureFlagPayload.class);
        } catch (Exception e) {
            log.error("解析 delayed_task.payload 失败, payloadLen={}", payload.length(), e);
            return null;
        }
    }

    private record FeatureFlagPayload(int featureFlagId, long expectedAtEpochMilli) {}
}


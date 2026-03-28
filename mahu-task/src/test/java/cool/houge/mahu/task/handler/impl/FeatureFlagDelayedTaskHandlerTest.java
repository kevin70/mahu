package cool.houge.mahu.task.handler.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import cool.houge.mahu.config.DelayedTaskTopic;
import cool.houge.mahu.repository.sys.FeatureFlagRepository;
import cool.houge.mahu.task.handler.ClaimedDelayedTask;
import cool.houge.mahu.task.handler.DelayedTaskCompletionResult;
import io.helidon.service.registry.Services;
import io.helidon.testing.junit5.Testing;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Testing.Test(perMethod = true)
class FeatureFlagDelayedTaskHandlerTest {

    private final FeatureFlagRepository featureFlagRepository = mock(FeatureFlagRepository.class);

    @BeforeEach
    void setUp() {
        Services.set(FeatureFlagRepository.class, featureFlagRepository);
    }

    @Test
    void supports_returns_true_for_enable_and_disable_topic(FeatureFlagDelayedTaskHandler handler) {
        assertTrue(handler.supports(DelayedTaskTopic.FEATURE_FLAG_ENABLE.topic()));
        assertTrue(handler.supports(DelayedTaskTopic.FEATURE_FLAG_DISABLE.topic()));
        assertFalse(handler.supports("UNKNOWN_TOPIC"));
    }

    @Test
    void handle_enable_topic_calls_enable_and_returns_complete(FeatureFlagDelayedTaskHandler handler) {
        var task = claimedTask(DelayedTaskTopic.FEATURE_FLAG_ENABLE.topic(), "-101");

        var result = handler.handle(task);

        assertEquals(DelayedTaskCompletionResult.COMPLETE, result);
        verify(featureFlagRepository).enableIfDue(eq(-101), any(Instant.class));
        verify(featureFlagRepository, never()).disableIfDue(eq(-101), any(Instant.class));
    }

    @Test
    void handle_disable_topic_calls_disable_and_returns_complete(FeatureFlagDelayedTaskHandler handler) {
        var task = claimedTask(DelayedTaskTopic.FEATURE_FLAG_DISABLE.topic(), "-202");

        var result = handler.handle(task);

        assertEquals(DelayedTaskCompletionResult.COMPLETE, result);
        verify(featureFlagRepository).disableIfDue(eq(-202), any(Instant.class));
        verify(featureFlagRepository, never()).enableIfDue(eq(-202), any(Instant.class));
    }

    @Test
    void handle_unknown_topic_returns_complete_and_does_not_call_repository(FeatureFlagDelayedTaskHandler handler) {
        var task = claimedTask("UNKNOWN_TOPIC", "-303");

        var result = handler.handle(task);

        assertEquals(DelayedTaskCompletionResult.COMPLETE, result);
        verify(featureFlagRepository, never()).enableIfDue(any(Integer.class), any(Instant.class));
        verify(featureFlagRepository, never()).disableIfDue(any(Integer.class), any(Instant.class));
    }

    private static ClaimedDelayedTask claimedTask(String topic, String referenceId) {
        return ClaimedDelayedTask.builder()
                .delayedTaskId(UUID.fromString("00000000-0000-0000-0000-000000000111"))
                .topic(topic)
                .referenceId(referenceId)
                .payload("{\"k\":\"v\"}")
                .idempotencyKey("idem-1")
                .delayUntil(Instant.parse("2026-03-28T12:00:00Z"))
                .build();
    }
}


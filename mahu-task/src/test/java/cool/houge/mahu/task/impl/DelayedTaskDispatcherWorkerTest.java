package cool.houge.mahu.task.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cool.houge.mahu.config.DelayedTaskTopic;
import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.repository.sys.DelayedTaskRepository;
import cool.houge.mahu.task.handler.ClaimedDelayedTask;
import cool.houge.mahu.task.handler.DelayedTaskCompletionResult;
import cool.houge.mahu.task.handler.DelayedTaskHandler;
import cool.houge.mahu.task.testing.HelpScopeTransTestBase;
import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.helidon.service.registry.Services;
import io.helidon.testing.junit5.Testing;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@Testing.Test(perMethod = true)
class DelayedTaskDispatcherWorkerTest extends HelpScopeTransTestBase {

    private static final Instant NOW = Instant.parse("2026-03-28T12:00:00Z");
    private static final String UNKNOWN_TOPIC = "UNKNOWN_TOPIC";
    private static final int BATCH_SIZE = 50;
    private static final int NEXT_ATTEMPTS = 1;
    private static final int DEFAULT_LEASE_SECONDS = DelayedTaskRepository.DEFAULT_LEASE_SECONDS;
    private static final Config CONFIG = Config.just(ConfigSources.create(Map.of(
                    "scheduling.delayed-task-dispatcher.enabled", "false"))
            .build());

    private final DelayedTaskRepository delayedTaskRepository = mock(DelayedTaskRepository.class);
    private final DelayedTaskHandler delayedTaskHandler = mock(DelayedTaskHandler.class);

    @BeforeEach
    void setUp() {
        Services.set(Config.class, CONFIG);
        Services.set(DelayedTaskRepository.class, delayedTaskRepository);
        Services.set(DelayedTaskHandler.class, delayedTaskHandler);
    }

    @Test
    void executeAt_claimed_and_handler_completes_updates_completed(DelayedTaskDispatcherWorker worker) {
        var taskId = UUID.fromString("00000000-0000-0000-0000-000000000101");
        var topic = DelayedTaskTopic.FEATURE_FLAG_ENABLE.topic();
        var candidate = new DelayedTask();
        candidate.setId(taskId);
        candidate.setTopic(topic);
        candidate.setReferenceId("ref-1");
        candidate.setPayload("{\"k\":\"v\"}");
        candidate.setIdempotencyKey("idem-1");
        candidate.setDelayUntil(NOW.minusSeconds(1));

        when(delayedTaskRepository.findDuePendingSkipLocked(NOW, BATCH_SIZE)).thenReturn(List.of(candidate));
        when(delayedTaskRepository.claimPending(taskId, NOW, DEFAULT_LEASE_SECONDS, NEXT_ATTEMPTS)).thenReturn(true);
        when(delayedTaskHandler.supports(topic)).thenReturn(true);
        when(delayedTaskHandler.handle(any())).thenReturn(DelayedTaskCompletionResult.COMPLETE);

        worker.executeAt(NOW);

        var claimedTaskCaptor = ArgumentCaptor.forClass(ClaimedDelayedTask.class);
        verify(delayedTaskHandler).handle(claimedTaskCaptor.capture());
        assertEquals(taskId, claimedTaskCaptor.getValue().getDelayedTaskId());
        assertEquals(topic, claimedTaskCaptor.getValue().getTopic());
        verify(delayedTaskRepository).claimPending(taskId, NOW, DEFAULT_LEASE_SECONDS, NEXT_ATTEMPTS);
        verify(delayedTaskRepository).complete(taskId);
        verify(delayedTaskRepository, never()).archive(taskId);
    }

    @Test
    void executeAt_handler_missing_archives_task(DelayedTaskDispatcherWorker worker) {
        var taskId = UUID.fromString("00000000-0000-0000-0000-000000000202");
        var candidate = new DelayedTask();
        candidate.setId(taskId);
        candidate.setTopic(UNKNOWN_TOPIC);
        candidate.setReferenceId("ref-2");
        candidate.setPayload("{\"k\":\"v\"}");
        candidate.setIdempotencyKey("idem-1");
        candidate.setDelayUntil(NOW.minusSeconds(1));

        when(delayedTaskRepository.findDuePendingSkipLocked(NOW, BATCH_SIZE)).thenReturn(List.of(candidate));
        when(delayedTaskRepository.claimPending(taskId, NOW, DEFAULT_LEASE_SECONDS, NEXT_ATTEMPTS)).thenReturn(true);
        when(delayedTaskHandler.supports(UNKNOWN_TOPIC)).thenReturn(false);

        worker.executeAt(NOW);

        verify(delayedTaskRepository).claimPending(taskId, NOW, DEFAULT_LEASE_SECONDS, NEXT_ATTEMPTS);
        verify(delayedTaskRepository).archive(taskId);
        verify(delayedTaskRepository, never()).complete(taskId);
        verify(delayedTaskHandler, never()).handle(any());
    }

    @Test
    void executeAt_handler_throws_keeps_processing_for_retry(DelayedTaskDispatcherWorker worker) {
        var taskId = UUID.fromString("00000000-0000-0000-0000-000000000303");
        var topic = DelayedTaskTopic.FEATURE_FLAG_ENABLE.topic();
        var candidate = new DelayedTask();
        candidate.setId(taskId);
        candidate.setTopic(topic);
        candidate.setReferenceId("ref-3");
        candidate.setPayload("{\"k\":\"v\"}");
        candidate.setIdempotencyKey("idem-1");
        candidate.setDelayUntil(NOW.minusSeconds(1));

        when(delayedTaskRepository.findDuePendingSkipLocked(NOW, BATCH_SIZE)).thenReturn(List.of(candidate));
        when(delayedTaskRepository.claimPending(taskId, NOW, DEFAULT_LEASE_SECONDS, NEXT_ATTEMPTS)).thenReturn(true);
        when(delayedTaskHandler.supports(topic)).thenReturn(true);
        when(delayedTaskHandler.handle(any())).thenThrow(new IllegalStateException("boom"));

        worker.executeAt(NOW);

        verify(delayedTaskRepository).claimPending(taskId, NOW, DEFAULT_LEASE_SECONDS, NEXT_ATTEMPTS);
        verify(delayedTaskRepository, never()).complete(taskId);
        verify(delayedTaskRepository, never()).archive(taskId);
    }

    @Test
    void executeAt_no_claimed_tasks_returns_without_dispatch(DelayedTaskDispatcherWorker worker) {
        when(delayedTaskRepository.findDuePendingSkipLocked(NOW, BATCH_SIZE)).thenReturn(List.of());

        worker.executeAt(NOW);

        verify(delayedTaskHandler, never()).supports(any());
        verify(delayedTaskHandler, never()).handle(any());
        verify(delayedTaskRepository, never()).claimPending(any(UUID.class), any(Instant.class), any(Integer.class), any(Integer.class));
        verify(delayedTaskRepository, never()).complete(any(UUID.class));
        verify(delayedTaskRepository, never()).archive(any(UUID.class));
    }
}

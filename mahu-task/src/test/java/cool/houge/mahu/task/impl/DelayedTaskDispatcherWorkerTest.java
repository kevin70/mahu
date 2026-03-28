package cool.houge.mahu.task.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.repository.sys.DelayedTaskRepository;
import cool.houge.mahu.task.handler.ClaimedDelayedTask;
import cool.houge.mahu.task.handler.DelayedTaskCompletionResult;
import cool.houge.mahu.task.handler.DelayedTaskHandler;
import io.helidon.service.registry.Services;
import io.helidon.testing.junit5.Testing;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@Testing.Test(perMethod = true)
class DelayedTaskDispatcherWorkerTest {

    private static final Instant NOW = Instant.parse("2026-03-28T12:00:00Z");

    private final DelayedTaskRepository delayedTaskRepository = mock(DelayedTaskRepository.class);
    private final DelayedTaskHandler delayedTaskHandler = mock(DelayedTaskHandler.class);

    @BeforeEach
    void setUp() {
        Services.set(DelayedTaskRepository.class, delayedTaskRepository);
        Services.set(DelayedTaskHandler.class, delayedTaskHandler);
    }

    @Test
    void executeAt_claimed_and_handler_completes_updates_completed(DelayedTaskDispatcherWorker worker) {
        var taskId = UUID.fromString("00000000-0000-0000-0000-000000000101");
        var candidate = delayedTask(taskId, "feature-flag.enable", "ref-1");

        when(delayedTaskRepository.findDuePendingSkipLocked(NOW, 50)).thenReturn(List.of(candidate));
        when(delayedTaskRepository.claimPending(taskId, NOW, DelayedTaskRepository.DEFAULT_LEASE_SECONDS, 1))
                .thenReturn(true);

        when(delayedTaskHandler.supports("feature-flag.enable")).thenReturn(true);
        when(delayedTaskHandler.handle(any())).thenReturn(DelayedTaskCompletionResult.COMPLETE);

        worker.executeAt(NOW);

        var claimedTaskCaptor = ArgumentCaptor.forClass(ClaimedDelayedTask.class);
        verify(delayedTaskHandler).handle(claimedTaskCaptor.capture());
        assertEquals(taskId, claimedTaskCaptor.getValue().getDelayedTaskId());
        assertEquals("feature-flag.enable", claimedTaskCaptor.getValue().getTopic());

        verify(delayedTaskRepository).complete(taskId);
        verify(delayedTaskRepository, never()).archive(taskId);
    }

    @Test
    void executeAt_handler_missing_archives_task(DelayedTaskDispatcherWorker worker) {
        var taskId = UUID.fromString("00000000-0000-0000-0000-000000000202");
        var candidate = delayedTask(taskId, "unknown.topic", "ref-2");

        when(delayedTaskRepository.findDuePendingSkipLocked(NOW, 50)).thenReturn(List.of(candidate));
        when(delayedTaskRepository.claimPending(taskId, NOW, DelayedTaskRepository.DEFAULT_LEASE_SECONDS, 1))
                .thenReturn(true);

        when(delayedTaskHandler.supports("unknown.topic")).thenReturn(false);

        worker.executeAt(NOW);

        verify(delayedTaskRepository).archive(taskId);
        verify(delayedTaskRepository, never()).complete(taskId);
        verify(delayedTaskHandler, never()).handle(any());
    }

    @Test
    void executeAt_handler_throws_keeps_processing_for_retry(DelayedTaskDispatcherWorker worker) {
        var taskId = UUID.fromString("00000000-0000-0000-0000-000000000303");
        var candidate = delayedTask(taskId, "feature-flag.enable", "ref-3");

        when(delayedTaskRepository.findDuePendingSkipLocked(NOW, 50)).thenReturn(List.of(candidate));
        when(delayedTaskRepository.claimPending(taskId, NOW, DelayedTaskRepository.DEFAULT_LEASE_SECONDS, 1))
                .thenReturn(true);

        when(delayedTaskHandler.supports("feature-flag.enable")).thenReturn(true);
        when(delayedTaskHandler.handle(any())).thenThrow(new IllegalStateException("boom"));

        worker.executeAt(NOW);

        verify(delayedTaskRepository, never()).complete(any(UUID.class));
        verify(delayedTaskRepository, never()).archive(eq(taskId));
    }

    @Test
    void executeAt_no_claimed_tasks_returns_without_dispatch(DelayedTaskDispatcherWorker worker) {
        when(delayedTaskRepository.findDuePendingSkipLocked(NOW, 50)).thenReturn(List.of());

        worker.executeAt(NOW);

        verify(delayedTaskHandler, never()).supports(any());
        verify(delayedTaskHandler, never()).handle(any());
        verify(delayedTaskRepository, never()).complete(any(UUID.class));
        verify(delayedTaskRepository, never()).archive(any(UUID.class));
    }

    private static DelayedTask delayedTask(UUID id, String topic, String referenceId) {
        var task = new DelayedTask();
        task.setId(id);
        task.setTopic(topic);
        task.setReferenceId(referenceId);
        task.setPayload("{\"k\":\"v\"}");
        task.setIdempotencyKey("idem-1");
        task.setDelayUntil(NOW.minusSeconds(1));
        return task;
    }

}


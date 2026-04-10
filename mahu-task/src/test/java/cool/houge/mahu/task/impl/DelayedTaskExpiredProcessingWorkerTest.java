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
import io.helidon.service.registry.Services;
import io.helidon.testing.junit5.Testing;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@Testing.Test(perMethod = true)
class DelayedTaskExpiredProcessingWorkerTest {

    private static final Instant NOW = Instant.parse("2026-03-28T12:00:00Z");

    private final DelayedTaskRepository delayedTaskRepository = mock(DelayedTaskRepository.class);

    @BeforeEach
    void setUp() {
        Services.set(DelayedTaskRepository.class, delayedTaskRepository);
    }

    @Test
    void executeAt_attempts_reached_max_transitions_to_failed(DelayedTaskExpiredProcessingWorker worker) {
        var taskId = UUID.fromString("00000000-0000-0000-0000-000000000111");
        var task = delayedTask(taskId, 3, 3);

        when(delayedTaskRepository.findExpiredProcessingSkipLocked(NOW, 50)).thenReturn(List.of(task));

        worker.executeAt(NOW);

        verify(delayedTaskRepository).transitionExpiredToFailed(taskId, NOW);
        verify(delayedTaskRepository, never())
                .transitionExpiredToPending(any(UUID.class), any(Instant.class), any(Instant.class));
    }

    @Test
    void executeAt_attempts_below_max_transitions_to_pending_with_backoff(DelayedTaskExpiredProcessingWorker worker) {
        var taskId = UUID.fromString("00000000-0000-0000-0000-000000000222");
        var task = delayedTask(taskId, 2, 5);

        when(delayedTaskRepository.findExpiredProcessingSkipLocked(NOW, 50)).thenReturn(List.of(task));
        when(delayedTaskRepository.transitionExpiredToPending(eq(taskId), eq(NOW), any(Instant.class)))
                .thenReturn(true);

        worker.executeAt(NOW);

        var retryAtCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(delayedTaskRepository).transitionExpiredToPending(eq(taskId), eq(NOW), retryAtCaptor.capture());
        assertEquals(NOW.plusSeconds(60), retryAtCaptor.getValue());
        verify(delayedTaskRepository, never()).transitionExpiredToFailed(any(UUID.class), any(Instant.class));
    }

    @Test
    void executeAt_null_attempts_and_max_use_defaults(DelayedTaskExpiredProcessingWorker worker) {
        var taskId = UUID.fromString("00000000-0000-0000-0000-000000000333");
        var task = delayedTask(taskId, null, null);

        when(delayedTaskRepository.findExpiredProcessingSkipLocked(NOW, 50)).thenReturn(List.of(task));
        when(delayedTaskRepository.transitionExpiredToPending(eq(taskId), eq(NOW), any(Instant.class)))
                .thenReturn(true);

        worker.executeAt(NOW);

        var retryAtCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(delayedTaskRepository).transitionExpiredToPending(eq(taskId), eq(NOW), retryAtCaptor.capture());
        // attempts 默认 0，退避秒数为 0。
        assertEquals(NOW, retryAtCaptor.getValue());
    }

    @Test
    void executeAt_high_attempts_caps_backoff_to_3600_seconds(DelayedTaskExpiredProcessingWorker worker) {
        var taskId = UUID.fromString("00000000-0000-0000-0000-000000000444");
        var task = delayedTask(taskId, 999, 1000);

        when(delayedTaskRepository.findExpiredProcessingSkipLocked(NOW, 50)).thenReturn(List.of(task));
        when(delayedTaskRepository.transitionExpiredToPending(eq(taskId), eq(NOW), any(Instant.class)))
                .thenReturn(true);

        worker.executeAt(NOW);

        var retryAtCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(delayedTaskRepository).transitionExpiredToPending(eq(taskId), eq(NOW), retryAtCaptor.capture());
        assertEquals(NOW.plusSeconds(3600), retryAtCaptor.getValue());
    }

    @Test
    void executeAt_no_candidates_does_nothing(DelayedTaskExpiredProcessingWorker worker) {
        when(delayedTaskRepository.findExpiredProcessingSkipLocked(NOW, 50)).thenReturn(List.of());

        worker.executeAt(NOW);

        verify(delayedTaskRepository, never())
                .transitionExpiredToPending(any(UUID.class), any(Instant.class), any(Instant.class));
        verify(delayedTaskRepository, never()).transitionExpiredToFailed(any(UUID.class), any(Instant.class));
    }

    private static DelayedTask delayedTask(UUID id, Integer attempts, Integer maxAttempts) {
        var task = new DelayedTask();
        task.setId(id);
        task.setAttempts(attempts);
        task.setMaxAttempts(maxAttempts);
        return task;
    }
}

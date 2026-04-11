package cool.houge.mahu.task.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.repository.sys.DelayedTaskRepository;
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

@Testing.Test(perMethod = true)
class DelayedTaskExpiredProcessingWorkerTest extends HelpScopeTransTestBase {

    private static final Instant NOW = Instant.parse("2026-03-28T12:00:00Z");
    private static final int BATCH_SIZE = 50;
    private static final Config CONFIG = Config.just(ConfigSources.create(Map.of(
                    "scheduling.delayed-task-expired-processing.enabled", "false"))
            .build());

    private final DelayedTaskRepository delayedTaskRepository = mock(DelayedTaskRepository.class);

    @BeforeEach
    void setUp() {
        Services.set(Config.class, CONFIG);
        Services.set(DelayedTaskRepository.class, delayedTaskRepository);
    }

    @Test
    void executeAt_attempts_reached_max_transitions_to_failed(DelayedTaskExpiredProcessingWorker worker) {
        var taskId = UUID.fromString("00000000-0000-0000-0000-000000000111");
        var task = new DelayedTask();
        task.setId(taskId);
        task.setAttempts(3);
        task.setMaxAttempts(3);

        when(delayedTaskRepository.findExpiredProcessingSkipLocked(NOW, BATCH_SIZE)).thenReturn(List.of(task));
        when(delayedTaskRepository.transitionExpiredToFailed(taskId, NOW)).thenReturn(true);

        worker.executeAt(NOW);

        verify(delayedTaskRepository).transitionExpiredToFailed(taskId, NOW);
        verify(delayedTaskRepository, never())
                .transitionExpiredToPending(any(UUID.class), any(Instant.class), any(Instant.class));
    }

    @Test
    void executeAt_attempts_below_max_transitions_to_pending_with_backoff(DelayedTaskExpiredProcessingWorker worker) {
        var taskId = UUID.fromString("00000000-0000-0000-0000-000000000222");
        var task = new DelayedTask();
        task.setId(taskId);
        task.setAttempts(2);
        task.setMaxAttempts(5);
        var retryAt = NOW.plusSeconds(60);

        when(delayedTaskRepository.findExpiredProcessingSkipLocked(NOW, BATCH_SIZE)).thenReturn(List.of(task));
        when(delayedTaskRepository.transitionExpiredToPending(taskId, NOW, retryAt)).thenReturn(true);

        worker.executeAt(NOW);

        verify(delayedTaskRepository).transitionExpiredToPending(taskId, NOW, retryAt);
        verify(delayedTaskRepository, never()).transitionExpiredToFailed(taskId, NOW);
    }

    @Test
    void executeAt_null_attempts_and_max_use_defaults(DelayedTaskExpiredProcessingWorker worker) {
        var taskId = UUID.fromString("00000000-0000-0000-0000-000000000333");
        var task = new DelayedTask();
        task.setId(taskId);
        task.setAttempts(null);
        task.setMaxAttempts(null);

        when(delayedTaskRepository.findExpiredProcessingSkipLocked(NOW, BATCH_SIZE)).thenReturn(List.of(task));
        when(delayedTaskRepository.transitionExpiredToPending(taskId, NOW, NOW)).thenReturn(true);

        worker.executeAt(NOW);

        verify(delayedTaskRepository).transitionExpiredToPending(taskId, NOW, NOW);
    }

    @Test
    void executeAt_high_attempts_caps_backoff_to_3600_seconds(DelayedTaskExpiredProcessingWorker worker) {
        var taskId = UUID.fromString("00000000-0000-0000-0000-000000000444");
        var task = new DelayedTask();
        task.setId(taskId);
        task.setAttempts(999);
        task.setMaxAttempts(1000);
        var retryAt = NOW.plusSeconds(3600);

        when(delayedTaskRepository.findExpiredProcessingSkipLocked(NOW, BATCH_SIZE)).thenReturn(List.of(task));
        when(delayedTaskRepository.transitionExpiredToPending(taskId, NOW, retryAt)).thenReturn(true);

        worker.executeAt(NOW);

        verify(delayedTaskRepository).transitionExpiredToPending(taskId, NOW, retryAt);
    }

    @Test
    void executeAt_no_candidates_does_nothing(DelayedTaskExpiredProcessingWorker worker) {
        when(delayedTaskRepository.findExpiredProcessingSkipLocked(NOW, BATCH_SIZE)).thenReturn(List.of());

        worker.executeAt(NOW);

        verify(delayedTaskRepository, never())
                .transitionExpiredToPending(any(UUID.class), any(Instant.class), any(Instant.class));
        verify(delayedTaskRepository, never()).transitionExpiredToFailed(any(UUID.class), any(Instant.class));
    }
}

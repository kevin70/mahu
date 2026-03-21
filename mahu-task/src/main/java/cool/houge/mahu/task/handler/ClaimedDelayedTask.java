package cool.houge.mahu.task.handler;

import java.time.Instant;
import java.util.UUID;

/// delayed_tasks 被 worker claim 成 PROCESSING 后，交给对应 topic handler 的最小上下文。
public record ClaimedDelayedTask(
    UUID delayedTaskId, String topic, String referenceId, String payload, Instant delayUntil) {}

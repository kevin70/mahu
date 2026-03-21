package cool.houge.mahu.task.event;

import java.util.UUID;

/// delayed_tasks 被 worker claim 成 PROCESSING 后发布的通用事件。
public record DelayedTaskClaimedEvent(
        UUID delayedTaskId,
        String topic,
        String payload
) {}


package cool.houge.mahu.model.command;

import cool.houge.mahu.config.DelayedTaskTopic;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

/// 延迟任务入队参数。
///
/// 用于承载按主题创建延迟任务时所需的公共字段，
/// 避免在 service public API 中暴露位置敏感的长参数列表。
@Getter
@Builder
public class EnqueueDelayedTaskCommand {
    /// 任务主题。
    private final DelayedTaskTopic topic;
    /// 业务引用 ID。
    private final String referenceId;
    /// 预期执行时间。
    private final Instant expectedAt;
    /// 幂等键。
    private final String idempotencyKey;
    /// 可选任务负载，支持 JSON 字符串或可序列化对象。
    private final Object payload;
}

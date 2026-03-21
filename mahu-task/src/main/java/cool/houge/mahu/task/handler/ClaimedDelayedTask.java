package cool.houge.mahu.task.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import io.helidon.service.registry.Services;
import java.time.Instant;
import java.util.UUID;

/// delayed_tasks 被 worker claim 成 PROCESSING 后，传递给 topic handler 的最小任务上下文。
///
/// 字段约定：
/// - delayedTaskId：延时任务唯一标识。
/// - topic：任务主题，用于路由到具体处理器。
/// - referenceId：业务引用 ID（通常为字符串形式的数字主键）。
/// - payload：任务携带的 JSON 载荷。
/// - idempotencyKey：幂等键，用于重复执行保护。
/// - delayUntil：任务理论触发时间（UTC 时间点）。
///
/// 该 record 同时提供便捷转换方法，避免在 handler 中重复解析逻辑。
public record ClaimedDelayedTask(
        UUID delayedTaskId,
        String topic,
        String referenceId,
        String payload,
        String idempotencyKey,
        Instant delayUntil) {

    private static final ObjectMapper OBJECT_MAPPER = Services.get(ObjectMapper.class);

    /// 将 referenceId 转换为 int，通常用于业务主键场景。
    public int referenceIdAsInt() {
        return Integer.parseInt(referenceId);
    }

    /// 将 referenceId 转换为 long，通常用于业务主键场景。
    public int referenceIdAsLong() {
        return Long.parseLong(referenceId);
    }

    /// 将 payload（JSON 字符串）反序列化为指定类型对象。
    public <T> T payloadAs(Class<T> targetType) {
        try {
            return OBJECT_MAPPER.readValue(payload, targetType);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    Strings.lenientFormat(
                            "payload 不是合法的 JSON 或无法转换为目标类型，delayedTaskId=%s，目标类型=%s",
                            delayedTaskId, targetType.getName()),
                    e);
        }
    }
}

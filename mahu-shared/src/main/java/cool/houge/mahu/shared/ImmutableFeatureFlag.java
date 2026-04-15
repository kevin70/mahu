package cool.houge.mahu.shared;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;

/// 不可变的功能开关缓存快照。
///
/// 对应 `sys.feature_flags` 的读模型，供缓存层与上层业务以线程安全方式共享。
/// 除基础启用标记外，还包含按时间窗生效/失效的判断所需字段。
@Value
@Builder
public class ImmutableFeatureFlag {
    /// 主键 ID。
    int id;
    /// 功能开关唯一编码。
    String code;
    /// 展示名称。
    String name;
    /// 业务说明。
    String description;
    /// 是否总开关启用。
    boolean enabled;
    /// 是否为系统预置功能。
    boolean preset;
    /// 生效起始时间；为空表示立即生效。
    Instant enableAt;
    /// 生效截止时间；为空表示不自动关闭。
    Instant disableAt;
    /// 展示排序值。
    int ordering;

    /// 按当前系统时间判断功能是否生效。
    public boolean isActive() {
        return isActive(Instant.now());
    }

    /// 按指定时间点判断功能是否生效。
    ///
    /// 规则为：总开关启用，且 `now` 不早于 `enableAt`，并且不晚于 `disableAt`。
    public boolean isActive(Instant now) {
        return enabled
                && (enableAt == null || !now.isBefore(enableAt))
                && (disableAt == null || !now.isAfter(disableAt));
    }
}

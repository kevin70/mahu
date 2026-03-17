package cool.houge.mahu.shared;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;

/// 功能开关快照模型。
///
/// 该类是对 `sys.feature_flags` 表的只读投影，用于在业务代码中以不可变对象的形式表达
/// 单个功能开关的当前配置与生效状态判断逻辑。通常由缓存服务
/// （如 `FeatureFlagCacheService`）构建后在各模块间共享。
@Value
@Builder
public class ImmutableFeatureFlag {

    /// 功能开关 ID
    int id;

    /// 功能代码
    String code;

    /// 功能名称
    String name;

    /// 功能描述
    String description;

    /// 当前是否启用
    boolean enabled;

    /// 是否为系统预置，禁止删除
    boolean preset;

    /// 定时开启时间，NULL=不启用定时
    Instant enableAt;

    /// 定时关闭时间，NULL=不启用定时
    Instant disableAt;

    /// 排序权重，值越大越靠前
    int ordering;

    /// 基于当前时间判断是否处于“生效中”状态
    public boolean isActive() {
        return isActive(Instant.now());
    }

    /// 基于指定时间判断是否处于“生效中”状态
    public boolean isActive(Instant now) {
        if (!enabled) {
            return false;
        }
        if (enableAt != null && now.isBefore(enableAt)) {
            return false;
        }
        if (disableAt != null && now.isAfter(disableAt)) {
            return false;
        }
        return true;
    }
}

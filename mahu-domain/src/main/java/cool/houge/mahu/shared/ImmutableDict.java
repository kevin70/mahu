package cool.houge.mahu.shared;

import lombok.Builder;
import lombok.Value;

/// 字典项快照模型。
///
/// 该类是对单条字典项的只读视图，通常由 `DictCacheService` 从数据库字典表加载并构建，
/// 用于在业务代码中以不可变对象的形式安全地传递和使用。
@Value
@Builder
public class ImmutableDict {
    /// 字典分组 ID
    String groupId;
    /// 字典代码
    int dc;
    /// 字典值
    String value;
    /// 标签
    String label;
    /// 是否启用
    boolean enabled;
    /// 排序值
    int ordering;
    /// 展示用颜色（如 CSS 色值）
    String color;
    /// 是否预置
    boolean preset;
}

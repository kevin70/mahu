package cool.houge.mahu.shared;

import lombok.Builder;
import lombok.Value;

/// 不可变的字典数据对象
///
/// 通过 Caffeine 缓存管理字典数据，使用不可变对象确保线程安全。
/// 此对象由 {@link DictCacheService} 创建和管理。
///
/// @author ZY (kzou227@qq.com)
@Value
@Builder
public class ImmutableDict {
    /// 字典分组 ID
    String groupId;
    /// 字典代码（主键）
    int dc;
    /// 字典值
    String value;
    /// 字典标签/显示文本
    String label;
    /// 是否启用
    boolean enabled;
    /// 排序值（降序）
    int ordering;
    /// 展示用颜色（CSS 色值）
    String color;
    /// 是否为预置字典
    boolean preset;
}

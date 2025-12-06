package cool.houge.mahu.shared;

import lombok.Builder;
import lombok.Value;

/// 字典
///
/// @author ZY (kzou227@qq.com)
@Value
@Builder
public class LcDict {
    /// 字典代码
    int dc;
    /// 字典类型 ID
    int dictTypeId;
    /// 字典值
    String value;
    /// 标签
    String label;
    /// 是否禁用
    boolean disabled;
    /// 排序值
    int ordering;
}

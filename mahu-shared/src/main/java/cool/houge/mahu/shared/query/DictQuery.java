package cool.houge.mahu.shared.query;

import lombok.Builder;
import lombok.Value;

/// 字典查询
@Value
@Builder
public class DictQuery {

    /// 类型 ID
    String id;
    /// 字典代码
    Integer dc;
}

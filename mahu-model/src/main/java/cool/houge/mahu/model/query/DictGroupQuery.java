package cool.houge.mahu.model.query;

import lombok.Builder;
import lombok.Value;

/// 字典查询
@Value
@Builder
public class DictGroupQuery {

    /// 分组 ID
    String groupId;
    /// 字典代码
    Integer dc;
}

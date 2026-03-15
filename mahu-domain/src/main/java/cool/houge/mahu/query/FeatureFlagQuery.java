package cool.houge.mahu.query;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FeatureFlagQuery {

    /// 是否启用
    Boolean enabled;

    /// 名称
    String name;

    /// 代码
    String code;
}

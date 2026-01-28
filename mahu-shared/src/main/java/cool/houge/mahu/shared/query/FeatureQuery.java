package cool.houge.mahu.shared.query;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FeatureQuery {

    /// 状态
    List<Integer> statusList;
    /// 名称
    String name;
}

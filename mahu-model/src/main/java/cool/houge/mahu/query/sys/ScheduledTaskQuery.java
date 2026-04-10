package cool.houge.mahu.query.sys;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ScheduledTaskQuery {

    /// 任务名称
    String taskName;
}

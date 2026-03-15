package cool.houge.mahu.query;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ScheduledTaskQuery {

    /// 任务名称
    String taskName;
}

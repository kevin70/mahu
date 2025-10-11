package cool.houge.mahu.task.internal;

import com.github.kagkarlsson.scheduler.task.ExecutionComplete;
import com.github.kagkarlsson.scheduler.task.schedule.Schedule;
import java.time.Instant;

/// 任务帮助类
///
/// @author ZY (kzou227@qq.com)
public class TaskHelper {

    /// 只执行一次的任务
    public static Schedule oneTime() {
        return new Schedule() {

            @Override
            public Instant getInitialExecutionTime(Instant now) {
                return now.plusSeconds(5);
            }

            @Override
            public Instant getNextExecutionTime(ExecutionComplete executionComplete) {
                return Instant.parse("2100-12-31T00:00:00Z");
            }

            @Override
            public boolean isDeterministic() {
                // Only deterministic if relative to a certain instant
                return false;
            }
        };
    }
}

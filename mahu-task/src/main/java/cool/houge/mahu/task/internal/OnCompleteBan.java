package cool.houge.mahu.task.internal;

import com.github.kagkarlsson.scheduler.task.CompletionHandler;
import com.github.kagkarlsson.scheduler.task.ExecutionComplete;
import com.github.kagkarlsson.scheduler.task.ExecutionOperations;
import java.time.Instant;

/// 软性禁止任务
///
/// @author ZY (kzou227@qq.com)
public class OnCompleteBan implements CompletionHandler<Void> {

    @Override
    public void complete(ExecutionComplete executionComplete, ExecutionOperations<Void> executionOperations) {
        executionOperations.reschedule(executionComplete, Instant.parse("2100-12-31T00:00:00Z"));
    }
}

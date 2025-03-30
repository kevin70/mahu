package cool.houge.mahu.task;

import com.github.f4b6a3.ulid.Ulid;
import com.github.kagkarlsson.scheduler.event.AbstractSchedulerListener;
import com.github.kagkarlsson.scheduler.task.ExecutionComplete;
import cool.houge.mahu.entity.ScheduledTask;
import cool.houge.mahu.entity.log.ScheduledExecutionLog;
import io.ebean.Database;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZoneId;
import java.util.Arrays;

/// 任务执行日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ExecutionLogSchedulerListener extends AbstractSchedulerListener {

    private static final Logger log = LogManager.getLogger();

    @Inject
    Database db;

    @Override
    public void onExecutionComplete(ExecutionComplete exec) {
        try {
            saveExecutionLog(exec);
        } catch (Throwable e) {
            log.error("保存定时任务执行日志 {}", exec.getExecution(), e);
        }
    }

    @Transactional
    void saveExecutionLog(ExecutionComplete executionComplete) {
        var execution = executionComplete.getExecution();
        var task = new ScheduledTask()
                .setTaskId(new ScheduledTask.TaskId()
                        .setTaskName(execution.getTaskName())
                        .setTaskInstance(execution.getId()));

        var finishedAt =
                executionComplete.getTimeDone().atZone(ZoneId.systemDefault()).toLocalDateTime();
        var startedAt = finishedAt.minus(executionComplete.getDuration());
        var bean = new ScheduledExecutionLog()
                .setScheduledTask(task)
                .setPickedBy(execution.pickedBy)
                .setStartedAt(startedAt)
                .setFinishedAt(finishedAt)
                .setSucceeded(ExecutionComplete.Result.OK.equals(executionComplete.getResult()));
        bean.setId(Ulid.fast().toString());

        executionComplete.getCause().ifPresent((cause) -> {
            var lines = Arrays.stream(cause.getStackTrace())
                    .map(StackTraceElement::toString)
                    .toList();
            bean.setCause(lines);
        });

        db.save(bean);
    }
}

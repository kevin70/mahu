package cool.houge.mahu.task.impl;

import com.github.kagkarlsson.scheduler.task.ExecutionContext;
import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import cool.houge.mahu.task.TaskSupplier;
import jakarta.inject.Singleton;

import java.time.Duration;
import java.time.Instant;

import static com.github.kagkarlsson.scheduler.task.schedule.Schedules.fixedDelay;

///
/// @author ZY (kzou227@qq.com)
@Singleton
public class HelloTask implements TaskSupplier {

    @Override
    public Task<Void> get() {
        return Tasks.recurring("hello-task", fixedDelay(Duration.ofSeconds(30))).execute(this::execute);
    }

    public void execute(TaskInstance<Void> taskInstance, ExecutionContext executionContext) {
        System.out.println("Hello Task Handler: " + Instant.now() + ", "
                + executionContext.getExecution().getExecutionTime());
    }
}

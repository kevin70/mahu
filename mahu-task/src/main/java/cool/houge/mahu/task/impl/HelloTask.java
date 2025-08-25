package cool.houge.mahu.task.impl;

import com.github.kagkarlsson.scheduler.task.ExecutionContext;
import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import io.helidon.service.registry.Service.Singleton;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

import static com.github.kagkarlsson.scheduler.task.schedule.Schedules.fixedDelay;

///
/// @author ZY (kzou227@qq.com)
@Singleton
public class HelloTask implements Supplier<Task<?>> {

    @Override
    public Task<Void> get() {
        return Tasks.recurring("这是一个测试任务", fixedDelay(Duration.ofSeconds(30))).execute(this::execute);
    }

    public void execute(TaskInstance<Void> taskInstance, ExecutionContext executionContext) {
        System.out.println("Hello Task Handler: " + Instant.now() + ", "
                + executionContext.getExecution().getExecutionTime());
    }
}

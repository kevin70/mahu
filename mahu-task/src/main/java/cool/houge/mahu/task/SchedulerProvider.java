package cool.houge.mahu.task;

import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.event.ExecutionChain;
import com.github.kagkarlsson.scheduler.task.CompletionHandler;
import com.github.kagkarlsson.scheduler.task.ExecutionContext;
import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import cool.houge.mahu.TraceIdGenerator;
import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.logging.common.HelidonMdc;
import io.helidon.service.registry.Service.PostConstruct;
import io.helidon.service.registry.Service.PreDestroy;
import io.helidon.service.registry.Service.RunLevel;
import io.helidon.service.registry.Service.Singleton;
import java.util.List;
import java.util.function.Supplier;
import javax.sql.DataSource;

/// 定时任务
///
/// @author ZY (kzou227@qq.com)
@Singleton
@RunLevel(RunLevel.STARTUP)
@Weight(Weighted.DEFAULT_WEIGHT + 900)
class SchedulerProvider implements Supplier<Scheduler> {

    final List<Task<?>> tasks;
    final Scheduler v;

    SchedulerProvider(DataSource ds, List<Task<?>> tasks, ExecutionLogSchedulerListener executionLogSchedulerListener) {
        this.tasks = List.copyOf(tasks);
        this.v = Scheduler.create(ds, this.tasks)
                .tableName("system.scheduled_task")
                .threads(Runtime.getRuntime().availableProcessors())
                .addExecutionInterceptor(this::trace)
                .addSchedulerListener(executionLogSchedulerListener)
                .enablePriority()
                .build();
    }

    @Override
    public Scheduler get() {
        return this.v;
    }

    @PostConstruct
    void init() {
        for (Task<?> task : tasks) {
            this.v.schedule(task.schedulableInstance("default"));
        }
        this.v.start();
    }

    @PreDestroy
    void destroy() {
        this.v.stop();
    }

    /// 在执行任务中增加日志追踪 ID
    CompletionHandler<?> trace(
            TaskInstance<?> taskInstance, ExecutionContext executionContext, ExecutionChain executionChain) {
        var traceId = TraceIdGenerator.generate();
        try {
            HelidonMdc.set("traceId", traceId);
            return executionChain.proceed(taskInstance, executionContext);
        } finally {
            System.out.println("traceId: " + traceId);
            HelidonMdc.remove("traceId");
        }
    }
}

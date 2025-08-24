package cool.houge.mahu.task;

import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.task.Task;
import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.service.registry.Service;
import io.helidon.service.registry.Service.RunLevel;
import io.helidon.service.registry.Service.Singleton;
import java.util.List;
import java.util.function.Supplier;
import javax.sql.DataSource;

/// 定时任务
///
/// @author ZY (kzou227@qq.com)
@Singleton
@RunLevel(RunLevel.SERVER)
@Weight(Weighted.DEFAULT_WEIGHT + 900)
class SchedulerProvider implements Supplier<Scheduler> {

    final List<Task<?>> tasks;
    final Scheduler v;

    SchedulerProvider(DataSource ds, List<Task<?>> tasks, ExecutionLogSchedulerListener executionLogSchedulerListener) {
        this.tasks = List.copyOf(tasks);
        this.v = Scheduler.create(ds, this.tasks)
                .tableName("system.scheduled_task")
                .threads(Runtime.getRuntime().availableProcessors())
                .addSchedulerListener(executionLogSchedulerListener)
                .enablePriority()
                .build();
    }

    @Override
    public Scheduler get() {
        return this.v;
    }

    @Service.PostConstruct
    void init() {
        for (Task<?> task : tasks) {
            this.v.schedule(task.schedulableInstance("default"));
        }
        this.v.start();
    }

    @Service.PreDestroy
    void destroy() {
        this.v.stop();
    }
}

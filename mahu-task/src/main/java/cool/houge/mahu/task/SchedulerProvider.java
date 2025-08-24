package cool.houge.mahu.task;

import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.task.Task;
import com.google.common.hash.Hashing;
import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.service.registry.Service.PostConstruct;
import io.helidon.service.registry.Service.PreDestroy;
import io.helidon.service.registry.Service.RunLevel;
import io.helidon.service.registry.Service.Singleton;
import java.nio.charset.StandardCharsets;
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

    @PostConstruct
    void init() {
        for (Task<?> task : tasks) {
            var id = Hashing.murmur3_128()
                    .hashString(task.getName(), StandardCharsets.UTF_8)
                    .toString();
            this.v.schedule(task.schedulableInstance(id));
        }
        this.v.start();
    }

    @PreDestroy
    void destroy() {
        this.v.stop();
    }
}

package cool.houge.mahu.task;

import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.task.Task;
import com.google.common.base.Strings;
import io.ebean.Database;
import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.service.registry.Service.PostConstruct;
import io.helidon.service.registry.Service.PreDestroy;
import io.helidon.service.registry.Service.RunLevel;
import io.helidon.service.registry.Service.Singleton;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 定时任务
///
/// @author ZY (kzou227@qq.com)
@Singleton
@RunLevel(RunLevel.STARTUP)
@Weight(Weighted.DEFAULT_WEIGHT + 900)
class SchedulerProvider implements Supplier<Scheduler> {

    private static final Logger log = LogManager.getLogger(SchedulerProvider.class);
    final List<Task<?>> tasks;
    final Scheduler v;

    SchedulerProvider(DataSource ds, List<Task<?>> tasks, Database db) {
        this.tasks = List.copyOf(tasks);
        this.v = Scheduler.create(ds, this.tasks)
                .tableName("sys.scheduled_task")
                .threads(Runtime.getRuntime().availableProcessors())
                .addExecutionInterceptor(new TraceExecutionInterceptor(db))
                .enablePriority()
                .build();
    }

    @Override
    public Scheduler get() {
        return this.v;
    }

    @PostConstruct
    void init() {
        var taskNames = new HashSet<String>();
        for (Task<?> task : tasks) {
            if (!taskNames.add(task.getName())) {
                throw new IllegalArgumentException(Strings.lenientFormat("任务名称重复: %s", task.getTaskName()));
            }
            this.v.scheduleIfNotExists(task.schedulableInstance("default"));
            log.info("注册定时任务: {}", task.getTaskName());
        }
        this.v.start();
    }

    @PreDestroy
    void destroy() {
        this.v.stop();
    }
}

package cool.houge.mahu.task;

import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.serializer.JacksonSerializer;
import com.github.kagkarlsson.scheduler.task.Task;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;

import javax.sql.DataSource;
import java.util.List;

/// 定时任务对象工厂
///
/// @author ZY (kzou227@qq.com)
@Factory
public class TaskBeanFactory {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Scheduler dbScheduler(
            DataSource dataSource, List<Task<Void>> tasks, ExecutionLogSchedulerListener executionLogSchedulerListener) {
        var scheduler = Scheduler.create(dataSource, tasks.toArray(new Task[0]))
                .missedHeartbeatsLimit(4)
                .threads(Runtime.getRuntime().availableProcessors())
                .serializer(new JacksonSerializer())
                .addSchedulerListener(executionLogSchedulerListener)
                .enablePriority()
                .build();

        for (Task<?> task : tasks) {
            scheduler.schedule(task.schedulableInstance("default"));
        }
        return scheduler;
    }
}

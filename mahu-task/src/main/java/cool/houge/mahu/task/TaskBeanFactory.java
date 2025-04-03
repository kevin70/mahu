package cool.houge.mahu.task;

import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.task.Task;
import cool.houge.mahu.config.SwitchConfig;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.util.List;

/// 定时任务对象工厂
///
/// @author ZY (kzou227@qq.com)
@Factory
public class TaskBeanFactory {

    private static final Logger log = LogManager.getLogger(TaskBeanFactory.class);

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Scheduler dbScheduler(
            SwitchConfig switchConfig,
            DataSource dataSource,
            List<TaskSupplier> taskSuppliers,
            ExecutionLogSchedulerListener executionLogSchedulerListener) {
        if (!switchConfig.schedulerOn()) {
            log.warn("功能定时任务调度未启用");
        }

        // 调度功能未启用时不注册定时任务
        var tasks = switchConfig.schedulerOn()
                ? taskSuppliers.stream().map(TaskSupplier::get).toList()
                : List.<Task<?>>of();

        //noinspection unchecked
        var scheduler = Scheduler.create(dataSource, (List<Task<?>>) tasks)
                .missedHeartbeatsLimit(4)
                .threads(Runtime.getRuntime().availableProcessors())
                .addSchedulerListener(executionLogSchedulerListener)
                .enablePriority()
                .build();

        for (Task<?> task : tasks) {
            scheduler.schedule(task.schedulableInstance("default"));
        }
        return scheduler;
    }
}

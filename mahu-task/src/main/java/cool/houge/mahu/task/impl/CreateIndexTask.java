package cool.houge.mahu.task.impl;

import static cool.houge.mahu.task.internal.TaskHelper.oneTime;

import com.github.kagkarlsson.scheduler.task.ExecutionContext;
import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import io.helidon.service.registry.Service;
import java.util.function.Supplier;

///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
public class CreateIndexTask implements Supplier<Task<?>> {

    @Override
    public Task<?> get() {
        return Tasks.recurring("创建索引", oneTime()).execute(this::execute);
    }

    private void execute(TaskInstance<Void> instance, ExecutionContext context) {
        System.out.println("创建索引");
    }
}

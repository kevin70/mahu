package cool.houge.mahu.task.impl;

import com.github.kagkarlsson.scheduler.task.CompletionHandler;
import com.github.kagkarlsson.scheduler.task.ExecutionContext;
import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import cool.houge.mahu.task.internal.OnCompleteBan;
import io.helidon.service.registry.Service;
import java.util.function.Supplier;

///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
public class CreateIndexTask implements Supplier<Task<?>> {

    @Override
    public Task<?> get() {
        return Tasks.custom("创建索引", Void.class)
                .defaultExecutionTime((o) -> o.plusSeconds(5))
                .execute(this::execute);
    }

    private CompletionHandler<Void> execute(TaskInstance<Void> instance, ExecutionContext context) {
        System.out.println("创建索引");
        return new OnCompleteBan();
    }
}

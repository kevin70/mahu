package cool.houge.mahu.task;

import com.github.kagkarlsson.scheduler.task.Task;

/// 定时任务提供者
///
/// @author ZY (kzou227@qq.com)
public interface TaskSupplier {

    Task<?> get();
}

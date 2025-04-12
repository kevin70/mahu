package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.system.ScheduledTask;
import io.ebean.Database;
import jakarta.inject.Singleton;

/// 定时任务
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ScheduledTaskRepository extends HBeanRepository<Void, ScheduledTask> {

    public ScheduledTaskRepository(Database db) {
        super(ScheduledTask.class, db);
    }
}

package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.FilterField;
import cool.houge.mahu.entity.system.ScheduledTask;
import cool.houge.mahu.entity.system.query.QScheduledTask;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

import java.time.Instant;
import java.util.List;

/// 定时任务
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ScheduledTaskRepository extends HBeanRepository<Void, ScheduledTask> {

    public ScheduledTaskRepository(Database db) {
        super(ScheduledTask.class, db);
    }

    /// 立即执行指定的任务
    public boolean execute(ScheduledTask entity) {
        // language=SQL
        var sql =
                "update system.scheduled_tasks set execution_time=:executionTime where task_name=:taskName and task_instance=:taskInstance and picked=false";
        return db().sqlUpdate(sql)
                        .setParameter("executionTime", Instant.now())
                        .setParameter("taskName", entity.getTaskId().getTaskName())
                        .setParameter("taskInstance", entity.getTaskId().getTaskInstance())
                        .execute()
                == 1;
    }

    /// 分页查询
    ///
    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | task_name | string |
    /// | task_instance | string |
    public PagedList<ScheduledTask> findPage(DataFilter dataFilter) {
        var qb = new QScheduledTask(db());
        var filterFields = List.of(
                FilterField.with(qb.taskId.taskName).filterName("task_name").build(),
                FilterField.with(qb.taskId.taskInstance)
                        .filterName("task_instance")
                        .build(),
                FilterField.with(qb.lastFailure).build());

        super.apply(dataFilter, filterFields, qb.query());
        return qb.findPagedList();
    }
}

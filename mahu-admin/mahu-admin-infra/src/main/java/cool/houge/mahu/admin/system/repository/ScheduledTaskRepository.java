package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.entity.system.ScheduledTask;
import cool.houge.mahu.entity.system.query.QScheduledTask;
import cool.houge.mahu.rsql.FilterItem;
import cool.houge.mahu.util.DataFilter;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
import java.util.List;

/// 定时任务
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ScheduledTaskRepository extends HBeanRepository<Void, ScheduledTask> {

    public ScheduledTaskRepository(Database db) {
        super(ScheduledTask.class, db);
    }

    /// 查询指定的定时任务并锁定
    public ScheduledTask findForUpdate(String taskName, String taskInstance) {
        return new QScheduledTask(db())
                .taskId
                .taskName
                .eq(taskName)
                .taskId
                .taskInstance
                .eq(taskInstance)
                .forUpdate()
                .findOne();
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
                FilterItem.of("task_name", qb.taskId.taskName),
                FilterItem.of("task_instance", qb.taskId.taskInstance),
                FilterItem.of(qb.lastFailure));

        super.apply(qb, dataFilter, filterFields);
        return qb.findPagedList();
    }
}

package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.domain.DataFilter;
import cool.houge.mahu.admin.entity.ScheduledTaskExeLog;
import cool.houge.mahu.entity.log.query.QScheduledExecutionLog;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;

/// 定时任务执行日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ScheduledExecutionLogRepository extends HBeanRepository<Long, ScheduledTaskExeLog> {

    public ScheduledExecutionLogRepository(Database db) {
        super(ScheduledTaskExeLog.class, db);
    }

    /// 分页查询
    ///
    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | task_id | string |
    public PagedList<ScheduledTaskExeLog> findPage(String taskId, DataFilter dataFilter) {
        return new QScheduledExecutionLog(db())
                .scheduledTask
                .id
                .eq(taskId)
                .also(o -> super.apply(o, dataFilter))
                .findPagedList();
    }
}

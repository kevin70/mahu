package cool.houge.mahu.admin.sys.repository;

import cool.houge.mahu.admin.entity.query.QScheduledTaskExeLog;
import cool.houge.mahu.domain.DataFilter;
import cool.houge.mahu.entity.sys.ScheduledTaskExeLog;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
import java.util.List;

/// 定时任务执行日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ScheduledExeLogRepository extends HBeanRepository<Long, ScheduledTaskExeLog> {

    public ScheduledExeLogRepository(Database db) {
        super(ScheduledTaskExeLog.class, db);
    }

    /// 分页查询
    ///
    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | task_name | string |
    public PagedList<ScheduledTaskExeLog> findPage(String taskName, DataFilter dataFilter) {
        return new QScheduledTaskExeLog(db())
                .scheduledTask
                .taskName
                .eq(taskName)
                .also(o -> super.apply(o.query(), dataFilter, List.of()))
                .findPagedList();
    }
}

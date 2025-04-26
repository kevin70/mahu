package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.log.ScheduledExecutionLog;
import cool.houge.mahu.entity.log.query.QScheduledExecutionLog;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

import java.util.List;

/// 定时任务执行日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ScheduledExecutionLogRepository extends HBeanRepository<Long, ScheduledExecutionLog> {

    public ScheduledExecutionLogRepository(Database db) {
        super(ScheduledExecutionLog.class, db);
    }

    /// 分页查询
    ///
    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | task_name | string |
    /// | task_instance | string |
    public PagedList<ScheduledExecutionLog> findPage(String taskName, String taskInstance, DataFilter dataFilter) {
        var qb = new QScheduledExecutionLog(db());
        qb.scheduledTask
                .taskId
                .taskName
                .eq(taskName)
                .scheduledTask
                .taskId
                .taskInstance
                .eq(taskInstance)
                // 排序
                .orderBy()
                .startedAt
                .desc();

        super.apply(dataFilter, List.of(), qb.query());
        return qb.findPagedList();
    }
}

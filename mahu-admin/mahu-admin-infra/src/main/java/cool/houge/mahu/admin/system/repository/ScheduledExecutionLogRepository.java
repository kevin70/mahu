package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.entity.log.ScheduledExecutionLog;
import cool.houge.mahu.entity.log.query.QScheduledExecutionLog;
import cool.houge.mahu.util.DataFilter;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
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
    /// | task_id | string |
    public PagedList<ScheduledExecutionLog> findPage(String taskId, DataFilter dataFilter) {
        var qb = new QScheduledExecutionLog(db());
        qb.scheduledTask
                .id
                .eq(taskId)
                // 排序
                .orderBy()
                .startedAt
                .desc();

        super.apply(qb, dataFilter, List.of());
        return qb.findPagedList();
    }
}

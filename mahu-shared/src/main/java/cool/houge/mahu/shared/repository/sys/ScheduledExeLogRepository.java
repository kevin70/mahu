package cool.houge.mahu.shared.repository.sys;

import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.ScheduledTaskExeLog;
import cool.houge.mahu.entity.sys.query.QScheduledTaskExeLog;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;

/// 定时任务执行日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ScheduledExeLogRepository extends HBeanRepository<Long, ScheduledTaskExeLog> {

    public ScheduledExeLogRepository(Database db) {
        super(ScheduledTaskExeLog.class, db);
    }

    /// 分页查询
    public PagedList<ScheduledTaskExeLog> findPage(String taskName, Page page) {
        var qb = new QScheduledTaskExeLog(db()).scheduledTask.taskName.eqIfPresent(taskName);
        return super.findPage(qb, page);
    }
}

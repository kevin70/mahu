package cool.houge.mahu.repository.sys;

import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.ScheduledTaskLog;
import cool.houge.mahu.entity.sys.query.QScheduledTaskLog;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;

/// 定时任务执行日志
///
/// 用于按任务名回溯调度执行记录。
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ScheduledExeLogRepository extends HBeanRepository<Long, ScheduledTaskLog> {

    public ScheduledExeLogRepository(Database db) {
        super(ScheduledTaskLog.class, db);
    }

    /// 分页查询
    ///
    /// `taskName` 为空时返回全部日志；非空时仅返回指定任务的执行日志。
    public PagedList<ScheduledTaskLog> findPage(String taskName, Page page) {
        var qb = new QScheduledTaskLog(db()).scheduledTask.taskName.eqIfPresent(taskName);
        return super.findPage(qb, page);
    }
}

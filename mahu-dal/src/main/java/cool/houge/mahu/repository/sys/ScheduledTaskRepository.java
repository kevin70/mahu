package cool.houge.mahu.repository.sys;

import com.google.common.base.Strings;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.ScheduledTask;
import cool.houge.mahu.entity.sys.query.QScheduledTask;
import cool.houge.mahu.query.sys.ScheduledTaskQuery;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;

/// 定时任务
///
/// 约定：任务唯一键为 `taskName`，并发执行窗口依赖行级锁控制。
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ScheduledTaskRepository extends HBeanRepository<Void, ScheduledTask> {

    public ScheduledTaskRepository(Database db) {
        super(ScheduledTask.class, db);
    }

    /// 查询指定任务并加行锁（FOR UPDATE）。
    ///
    /// 用于“读-改-写”同一任务状态时避免并发抢占。
    public ScheduledTask findForUpdate(String taskName) {
        return new QScheduledTask(db()).taskName.eq(taskName).forUpdate().findOne();
    }

    /// 分页查询
    ///
    /// `taskName` 为空时不过滤；非空时按包含关系（icontains）查询。
    public PagedList<ScheduledTask> findPage(ScheduledTaskQuery query, Page page) {
        var qb = new QScheduledTask(db());
        if (!Strings.isNullOrEmpty(query.getTaskName())) {
            qb.taskName.icontains(query.getTaskName());
        }

        return super.findPage(qb, page);
    }
}

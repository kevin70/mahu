package cool.houge.mahu.shared.repository.sys;

import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.ScheduledTask;
import cool.houge.mahu.entity.sys.query.QScheduledTask;
import cool.houge.mahu.shared.query.ScheduledTaskQuery;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;

/// 定时任务
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ScheduledTaskRepository extends HBeanRepository<Void, ScheduledTask> {

    public ScheduledTaskRepository(Database db) {
        super(ScheduledTask.class, db);
    }

    /// 查询指定的定时任务并锁定
    public ScheduledTask findForUpdate(String taskName) {
        return new QScheduledTask(db()).taskName.eq(taskName).forUpdate().findOne();
    }

    /// 分页查询
    ///
    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | id | string |
    /// | task_name | string |
    public PagedList<ScheduledTask> findPage(ScheduledTaskQuery query, Page page) {
        var qb = new QScheduledTask(db());
        return super.findPage(qb, page);
    }
}

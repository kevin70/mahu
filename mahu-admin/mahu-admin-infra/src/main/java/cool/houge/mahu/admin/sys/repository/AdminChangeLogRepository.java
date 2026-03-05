package cool.houge.mahu.admin.sys.repository;

import cool.houge.mahu.entity.sys.query.QAdminChangeLog;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.AdminChangeLog;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;

/// 管理员操作变更日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AdminChangeLogRepository extends HBeanRepository<String, AdminChangeLog> {

    public AdminChangeLogRepository(Database db) {
        super(AdminChangeLog.class, db);
    }

    /// 管理员操作变更日志
    public PagedList<AdminChangeLog> findPage(Integer adminId, Page page) {
        var qb = new QAdminChangeLog(db()).adminId.eqIfPresent(adminId).id.desc();
        return super.findPage(qb, page);
    }
}

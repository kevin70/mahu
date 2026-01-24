package cool.houge.mahu.admin.sys.repository;

import cool.houge.mahu.admin.entity.AdminAuditLog;
import cool.houge.mahu.admin.entity.query.QAdminAuditLog;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
import java.util.UUID;

/// 管理员操作审计日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AdminAuditLogRepository extends HBeanRepository<UUID, AdminAuditLog> {

    public AdminAuditLogRepository(Database db) {
        super(AdminAuditLog.class, db);
    }

    /// 管理员操作审计日志
    public PagedList<AdminAuditLog> findPage(Integer adminId, Page page) {
        var qb = new QAdminAuditLog(db()).adminId.eqIfPresent(adminId).id.desc();
        return super.findPage(qb, page);
    }
}

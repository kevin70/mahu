package cool.houge.mahu.admin.sys.repository;

import cool.houge.mahu.admin.entity.AdminAccessLog;
import cool.houge.mahu.admin.entity.query.QAdminAccessLog;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
import java.util.UUID;

/// 管理员访问记录
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AdminAccessLogRepository extends HBeanRepository<UUID, AdminAccessLog> {

    public AdminAccessLogRepository(Database db) {
        super(AdminAccessLog.class, db);
    }

    /// 管理员后台访问记录
    public PagedList<AdminAccessLog> findPage(Integer adminId, Page page) {
        var qb = new QAdminAccessLog(db()).adminId.eqIfPresent(adminId).id.desc();
        return super.findPage(qb, page);
    }
}

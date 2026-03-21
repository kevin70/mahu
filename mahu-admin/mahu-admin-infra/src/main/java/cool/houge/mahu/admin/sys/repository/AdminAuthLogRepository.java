package cool.houge.mahu.admin.sys.repository;

import cool.houge.mahu.admin.sys.query.AdminLogQuery;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.AdminAuthLog;
import cool.houge.mahu.entity.sys.query.QAdminAuthLog;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
import java.util.UUID;
import org.jspecify.annotations.NonNull;

/// 管理员认证日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AdminAuthLogRepository extends HBeanRepository<UUID, AdminAuthLog> {

    public AdminAuthLogRepository(Database db) {
        super(AdminAuthLog.class, db);
    }

    /// 管理员后台访问记录
    public PagedList<AdminAuthLog> findPage(@NonNull AdminLogQuery query, Page page) {
        var qb = new QAdminAuthLog(db()).adminId.eqIfPresent(query.getAdminId());
        var createdAtRange = query.getCreatedAtRange();
        createdAtRange.from().ifPresent(qb.createdAt::ge);
        createdAtRange.to().ifPresent(qb.createdAt::le);
        qb.id.desc();
        return super.findPage(qb, page);
    }
}

package cool.houge.mahu.admin.sys.repository;

import cool.houge.mahu.admin.dto.AdminLogQuery;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.domain.DateRange;
import cool.houge.mahu.entity.sys.AdminChangeLog;
import cool.houge.mahu.entity.sys.query.QAdminChangeLog;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
import org.jspecify.annotations.NonNull;

/// 管理员操作变更日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AdminChangeLogRepository extends HBeanRepository<String, AdminChangeLog> {

    public AdminChangeLogRepository(Database db) {
        super(AdminChangeLog.class, db);
    }

    /// 管理员操作变更日志
    public PagedList<AdminChangeLog> findPage(@NonNull AdminLogQuery query, Page page) {
        var qb = new QAdminChangeLog(db()).adminId.eqIfPresent(query.getAdminId());
        var createdAtRange = query.getCreatedAtRange();
        createdAtRange.from().ifPresent(qb.createdAt::ge);
        createdAtRange.to().ifPresent(qb.createdAt::le);
        qb.id.desc();
        return super.findPage(qb, page);
    }
}

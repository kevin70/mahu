package cool.houge.mahu.admin.sys.repository;

import cool.houge.mahu.admin.dto.AdminLogQuery;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.domain.DateRange;
import cool.houge.mahu.entity.sys.AdminAccessLog;
import cool.houge.mahu.entity.sys.query.QAdminAccessLog;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
import java.util.UUID;
import org.jspecify.annotations.NonNull;

/// 管理员访问记录
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AdminAccessLogRepository extends HBeanRepository<UUID, AdminAccessLog> {

    public AdminAccessLogRepository(Database db) {
        super(AdminAccessLog.class, db);
    }

    /// 管理员后台访问记录
    public PagedList<AdminAccessLog> findPage(@NonNull AdminLogQuery query, Page page) {
        var qb = new QAdminAccessLog(db()).adminId.eqIfPresent(query.getAdminId());
        var createdAtRange = query.getCreatedAtRange();
        createdAtRange.from().ifPresent(qb.createdAt::ge);
        createdAtRange.to().ifPresent(qb.createdAt::le);
        qb.id.desc();
        return super.findPage(qb, page);
    }
}

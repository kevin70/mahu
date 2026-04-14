package cool.houge.mahu.repository.sys;

import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.AdminLoginLog;
import cool.houge.mahu.entity.sys.query.QAdminLoginLog;
import cool.houge.mahu.model.query.AdminLogQuery;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
import java.util.UUID;
import org.jspecify.annotations.NonNull;

/// 管理员登录日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AdminLoginLogRepository extends HBeanRepository<UUID, AdminLoginLog> {

    public AdminLoginLogRepository(Database db) {
        super(AdminLoginLog.class, db);
    }

    /// 管理员登录日志分页查询
    public PagedList<AdminLoginLog> findPage(@NonNull AdminLogQuery query, Page page) {
        var qb = new QAdminLoginLog(db())
                .adminId
                .eqIfPresent(query.getAdminId())
                .username
                .eqIfPresent(query.getUsername());
        var createdAtRange = query.getCreatedAtRange();
        createdAtRange.applyFromTo(qb.createdAt::ge, qb.createdAt::le);
        qb.id.desc();
        return super.findPage(qb, page);
    }
}

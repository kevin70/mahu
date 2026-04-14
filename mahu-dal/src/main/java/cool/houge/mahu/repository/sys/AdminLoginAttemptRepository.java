package cool.houge.mahu.repository.sys;

import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.AdminLoginAttempt;
import cool.houge.mahu.entity.sys.query.QAdminLoginAttempt;
import cool.houge.mahu.model.query.AdminLogQuery;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
import java.util.UUID;
import org.jspecify.annotations.NonNull;

/// 管理员登录尝试记录
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AdminLoginAttemptRepository extends HBeanRepository<UUID, AdminLoginAttempt> {

    public AdminLoginAttemptRepository(Database db) {
        super(AdminLoginAttempt.class, db);
    }

    /// 管理员登录尝试分页查询
    public PagedList<AdminLoginAttempt> findPage(@NonNull AdminLogQuery query, Page page) {
        var qb = new QAdminLoginAttempt(db()).adminId.eqIfPresent(query.getAdminId());
        var createdAtRange = query.getCreatedAtRange();
        createdAtRange.applyFromTo(qb.createdAt::ge, qb.createdAt::le);
        qb.id.desc();
        return super.findPage(qb, page);
    }
}

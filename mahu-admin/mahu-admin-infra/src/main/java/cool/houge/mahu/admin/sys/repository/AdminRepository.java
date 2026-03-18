package cool.houge.mahu.admin.sys.repository;

import com.google.common.base.Strings;
import cool.houge.mahu.admin.sys.query.AdminQuery;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.Admin;
import cool.houge.mahu.entity.sys.query.QAdmin;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
import jakarta.persistence.EntityNotFoundException;

/// 管理员
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AdminRepository extends HBeanRepository<Integer, Admin> {

    public AdminRepository(Database db) {
        super(Admin.class, db);
    }

    public Admin obtainById(Integer id) {
        return findByIdOrEmpty(id)
                .orElseThrow(() -> new EntityNotFoundException(Strings.lenientFormat("未找到用户[id=%s]", id)));
    }

    public Admin findByUsername(String username) {
        return new QAdmin(db()).username.eq(username).findOne();
    }

    /// 分页查询
    public PagedList<Admin> findPage(AdminQuery query, Page page) {
        var qb = new QAdmin(db());
        if (!Strings.isNullOrEmpty(query.getUsername())) {
            qb.username.eq(query.getUsername());
        }
        if (!query.getStatusList().isEmpty()) {
            qb.status.in(query.getStatusList());
        }
        return super.findPage(qb, page);
    }
}

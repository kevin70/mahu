package cool.houge.mahu.admin.sys.repository;

import cool.houge.mahu.admin.entity.query.QRole;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.Role;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;

/// 角色
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class RoleRepository extends HBeanRepository<Integer, Role> {

    public RoleRepository(Database db) {
        super(Role.class, db);
    }

    /// 分页查询
    public PagedList<Role> findPage(Page page) {
        var qb = new QRole(db());
        return super.findPage(qb, page);
    }
}

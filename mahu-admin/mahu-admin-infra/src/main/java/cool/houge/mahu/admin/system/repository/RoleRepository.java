package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.system.Role;
import cool.houge.mahu.entity.system.query.QRole;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 角色
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class RoleRepository extends HBeanRepository<Integer, Role> {

    @Inject
    public RoleRepository(Database database) {
        super(Role.class, database);
    }

    /// 分页查询
    public PagedList<Role> findPage(DataFilter filter) {
        var qb = new QRole(db());
        apply(qb.query(), filter);
        return qb.findPagedList();
    }
}

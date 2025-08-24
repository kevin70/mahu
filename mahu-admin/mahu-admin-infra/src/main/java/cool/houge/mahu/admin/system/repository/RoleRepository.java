package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.entity.system.Role;
import cool.houge.mahu.entity.system.query.QRole;
import cool.houge.mahu.rsql.FilterItem;
import cool.houge.mahu.util.DataFilter;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
import java.util.List;

/// 角色
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class RoleRepository extends HBeanRepository<Integer, Role> {

    public RoleRepository(Database db) {
        super(Role.class, db);
    }

    /// 分页查询
    ///
    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | created_at | date-time |
    /// | updated_at | date-time |
    /// | name | string |
    /// | ordering | int |
    public PagedList<Role> findPage(DataFilter dataFilter) {
        var qb = new QRole(db());
        var filterItems = List.of(
                FilterItem.of(qb.createdAt),
                FilterItem.of(qb.updatedAt),
                FilterItem.of(qb.name),
                FilterItem.of(qb.ordering));

        super.apply(qb, dataFilter, filterItems);
        return qb.findPagedList();
    }
}

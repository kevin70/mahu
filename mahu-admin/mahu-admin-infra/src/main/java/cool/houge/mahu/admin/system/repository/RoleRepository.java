package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.FilterField;
import cool.houge.mahu.entity.system.Role;
import cool.houge.mahu.entity.system.query.QRole;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;

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
        var filterFields = List.of(
                FF_CREATED_AT,
                FF_UPDATED_AT,
                FilterField.builder().with(qb.name).build(),
                FilterField.builder().with(qb.ordering).build());

        super.apply(dataFilter, filterFields, qb);
        return qb.findPagedList();
    }
}

package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.RSQLContext;
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
    ///
    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | create_time | date-time |
    /// | update_time | date-time |
    /// | name | string |
    /// | ordering | int |
    public PagedList<Role> findPage(DataFilter dataFilter) {
        var qb = new QRole(db());
        var rsqlCtx = RSQLContext.of(qb)
                .property("create_time", qb.createTime)
                .property("update_time", qb.updateTime)
                .property(qb.name)
                .property(qb.ordering);
        super.apply(dataFilter, rsqlCtx);
        return qb.findPagedList();
    }
}

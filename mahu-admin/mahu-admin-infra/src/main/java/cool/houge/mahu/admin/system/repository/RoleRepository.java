package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.domain.DataFilter;
import cool.houge.mahu.entity.system.Role;
import cool.houge.mahu.entity.system.query.QRole;
import cool.houge.mahu.rsql.FilterItem;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
import java.util.List;
import org.jspecify.annotations.NonNull;

/// 角色
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class RoleRepository extends HBeanRepository<Integer, Role> {

    public RoleRepository(Database db) {
        super(Role.class, db);
    }

    @Override
    protected @NonNull List<FilterItem> filterableItems() {
        return List.of(
                FilterItem.of(QRole.Alias.createdAt),
                FilterItem.of(QRole.Alias.updatedAt),
                FilterItem.of(QRole.Alias.name),
                FilterItem.of(QRole.Alias.ordering));
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
        return new QRole(db()).also(o -> super.apply(o, dataFilter)).findPagedList();
    }
}

package cool.houge.mahu.admin.system.repository;

import com.google.common.base.Strings;
import cool.houge.mahu.domain.DataFilter;
import cool.houge.mahu.entity.system.Admin;
import cool.houge.mahu.entity.system.AdminStatus;
import cool.houge.mahu.entity.system.query.QAdmin;
import cool.houge.mahu.rsql.FilterItem;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.jspecify.annotations.NonNull;

/// 管理员
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AdminRepository extends HBeanRepository<Long, Admin> {

    public AdminRepository(Database db) {
        super(Admin.class, db);
    }

    public Admin obtainById(long id) {
        return findByIdOrEmpty(id)
                .orElseThrow(() -> new EntityNotFoundException(Strings.lenientFormat("未找到用户[id=%s]", id)));
    }

    public Admin findByUsername(String username) {
        return new QAdmin(db()).username.eq(username).findOne();
    }

    /// 分页查询
    ///
    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | created_at | date-time |
    /// | updated_at | date-time |
    /// | nickname | string |
    /// | status | enum |
    public PagedList<Admin> findPage(DataFilter dataFilter) {
        return new QAdmin(db()).also(o -> super.apply(o, dataFilter)).findPagedList();
    }

    @Override
    protected @NonNull List<FilterItem> filterableItems() {
        return List.of(
                FilterItem.of(QAdmin.Alias.createdAt),
                FilterItem.of(QAdmin.Alias.updatedAt),
                FilterItem.of(QAdmin.Alias.nickname),
                FilterItem.of(QAdmin.Alias.status, AdminStatus::valueOf)
                //
                );
    }
}

package cool.houge.mahu.admin.sys.repository;

import cool.houge.mahu.admin.entity.AdminAccessLog;
import cool.houge.mahu.admin.entity.query.QAdminAccessLog;
import cool.houge.mahu.domain.DataFilter;
import cool.houge.mahu.rsql.FilterItem;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
import java.util.List;
import org.jspecify.annotations.NonNull;

/// 管理员访问记录
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AdminAccessLogRepository extends HBeanRepository<Long, AdminAccessLog> {

    public AdminAccessLogRepository(Database db) {
        super(AdminAccessLog.class, db);
    }

    /// 管理员后台访问记录
    ///
    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | created_at | date-time |
    /// | admin_id | int |
    /// | ip_addr | string |
    public PagedList<AdminAccessLog> findPage(DataFilter dataFilter) {
        return new QAdminAccessLog(db())
                .also(o -> super.apply(o.query(), dataFilter))
                .findPagedList();
    }

    @Override
    protected @NonNull List<FilterItem> filterableItems() {
        return List.of(
                FilterItem.of(QAdminAccessLog.Alias.createdAt),
                FilterItem.of(QAdminAccessLog.Alias.adminId),
                FilterItem.of(QAdminAccessLog.Alias.ipAddr)
                //
                );
    }
}

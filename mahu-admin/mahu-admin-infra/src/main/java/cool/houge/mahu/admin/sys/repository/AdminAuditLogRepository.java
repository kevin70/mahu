package cool.houge.mahu.admin.sys.repository;

import cool.houge.mahu.admin.entity.AdminAuditLog;
import cool.houge.mahu.admin.entity.query.QAdminAuditLog;
import cool.houge.mahu.domain.DataFilter;
import cool.houge.mahu.rsql.FilterItem;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
import java.util.List;

/// 管理员操作审计日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AdminAuditLogRepository extends HBeanRepository<Long, AdminAuditLog> {

    public AdminAuditLogRepository(Database db) {
        super(AdminAuditLog.class, db);
    }

    /// 管理员操作审计日志
    ///
    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | created_at | date-time |
    /// | admin_id | int |
    /// | ip_addr | string |
    public PagedList<AdminAuditLog> findPage(DataFilter dataFilter) {
        return new QAdminAuditLog(db())
                .also(o -> super.apply(o.query(), dataFilter, filterableItems()))
                .findPagedList();
    }

    List<FilterItem> filterableItems() {
        return List.of(
                FilterItem.of(QAdminAuditLog.Alias.createdAt),
                FilterItem.of(QAdminAuditLog.Alias.adminId),
                FilterItem.of(QAdminAuditLog.Alias.ipAddr)
                //
                );
    }
}

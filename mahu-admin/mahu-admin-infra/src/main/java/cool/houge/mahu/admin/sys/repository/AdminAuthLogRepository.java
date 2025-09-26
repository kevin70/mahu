package cool.houge.mahu.admin.sys.repository;

import cool.houge.mahu.admin.entity.AdminAuthLog;
import cool.houge.mahu.admin.entity.query.QAdminAuthLog;
import cool.houge.mahu.domain.DataFilter;
import cool.houge.mahu.rsql.FilterItem;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
import java.util.List;

/// 管理员认证日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AdminAuthLogRepository extends HBeanRepository<Long, AdminAuthLog> {

    public AdminAuthLogRepository(Database db) {
        super(AdminAuthLog.class, db);
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
    public PagedList<AdminAuthLog> findPage(DataFilter dataFilter) {
        return new QAdminAuthLog(db())
                .also(o -> super.apply(o.query(), dataFilter, filterableItems()))
                .findPagedList();
    }

    List<FilterItem> filterableItems() {
        return List.of(
                FilterItem.of(QAdminAuthLog.Alias.createdAt),
                FilterItem.of(QAdminAuthLog.Alias.adminId),
                FilterItem.of(QAdminAuthLog.Alias.ipAddr)
                //
                );
    }
}

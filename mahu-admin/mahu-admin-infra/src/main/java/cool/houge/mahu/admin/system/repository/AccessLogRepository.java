package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.admin.entity.AdminAccessLog;
import cool.houge.mahu.admin.entity.query.QAdminAccessLog;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.RSQLContext;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

/// 后台访问记录
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AccessLogRepository extends HBeanRepository<Long, AdminAccessLog> {

    public AccessLogRepository(Database db) {
        super(AdminAccessLog.class, db);
    }

    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | created_at | date-time |
    /// | admin_id | int |
    /// | ip_addr | string |
    public PagedList<AdminAccessLog> findPage(DataFilter dataFilter) {
        var qb = new QAdminAccessLog(db());
        var rsqlCtx = RSQLContext.of(qb)
                .property("created_at", qb.createdAt)
                .property("admin_id", qb.adminId)
                .property("ip_addr", qb.ipAddr);
        super.apply(dataFilter, rsqlCtx);
        return qb.findPagedList();
    }
}

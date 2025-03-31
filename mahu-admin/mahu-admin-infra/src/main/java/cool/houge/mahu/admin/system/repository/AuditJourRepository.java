package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.admin.entity.AdminAuditLog;
import cool.houge.mahu.admin.entity.query.QAdminAuditLog;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.RSQLContext;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

/// 操作审计
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AuditJourRepository extends HBeanRepository<Long, AdminAuditLog> {

    public AuditJourRepository(Database db) {
        super(AdminAuditLog.class, db);
    }

    /// 分页查询
    ///
    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | created_at | date-time |
    /// | admin_id | int |
    /// | ip_addr | string |
    public PagedList<AdminAuditLog> findPage(DataFilter dataFilter) {
        var qb = new QAdminAuditLog(db());
        var rsqlCtx = RSQLContext.of(qb)
                .property("created_at", qb.createdAt)
                .property("admin_id", qb.adminId)
                .property("ip_addr", qb.ipAddr);
        super.apply(dataFilter, rsqlCtx);
        return qb.findPagedList();
    }
}

package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.RSQLContext;
import cool.houge.mahu.entity.system.AccessLog;
import cool.houge.mahu.entity.system.query.QAccessLog;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

/// 后台访问记录
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AccessLogRepository extends HBeanRepository<Long, AccessLog> {

    public AccessLogRepository(Database db) {
        super(AccessLog.class, db);
    }

    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | created_at | date-time |
    /// | employee_id | int |
    /// | ip_addr | string |
    public PagedList<AccessLog> findPage(DataFilter dataFilter) {
        var qb = new QAccessLog(db());
        var rsqlCtx = RSQLContext.of(qb)
                .property("created_at", qb.createdAt)
                .property("employee_id", qb.employeeId)
                .property("ip_addr", qb.ipAddr);
        super.apply(dataFilter, rsqlCtx);
        return qb.findPagedList();
    }
}

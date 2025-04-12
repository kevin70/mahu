package cool.houge.mahu.admin.repository;

import cool.houge.mahu.admin.entity.AdminAccessLog;
import cool.houge.mahu.admin.entity.AdminAuditLog;
import cool.houge.mahu.admin.entity.AdminAuthLog;
import cool.houge.mahu.admin.entity.query.QAdminAccessLog;
import cool.houge.mahu.admin.entity.query.QAdminAuditLog;
import cool.houge.mahu.admin.entity.query.QAdminAuthLog;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.FilterField;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

import java.util.List;

/// 业务日志数据仓库
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class LogRepository extends HBeanRepository<Void, Void> {

    public LogRepository(Database db) {
        super(Void.class, db);
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
    public PagedList<AdminAuthLog> findPage4AdminAuthLog(DataFilter dataFilter) {
        var qb = new QAdminAuthLog(db());
        var filterFields = List.of(
            FF_CREATED_AT,
            FilterField.with(qb.adminId).build(),
            FilterField.with(qb.ipAddr).build());

        super.apply(dataFilter, filterFields, qb.query());
        return qb.findPagedList();
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
    public PagedList<AdminAccessLog> findPage4AdminAccessLog(DataFilter dataFilter) {
        var qb = new QAdminAccessLog(db());
        var filterFields = List.of(
                FF_CREATED_AT,
                FilterField.with(qb.adminId).build(),
                FilterField.with(qb.ipAddr).build());

        super.apply(dataFilter, filterFields, qb.query());
        return qb.findPagedList();
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
    public PagedList<AdminAuditLog> findPage4AdminAuditLog(DataFilter dataFilter) {
        var qb = new QAdminAuditLog(db());
        var filterFields = List.of(
            FF_CREATED_AT,
            FilterField.with(qb.adminId).build(),
            FilterField.with(qb.ipAddr).build());

        super.apply(dataFilter, filterFields, qb.query());
        return qb.findPagedList();
    }
}

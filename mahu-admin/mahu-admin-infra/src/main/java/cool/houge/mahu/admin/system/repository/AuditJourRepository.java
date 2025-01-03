package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.RSQLContext;
import cool.houge.mahu.entity.system.AuditJour;
import cool.houge.mahu.entity.system.query.QAuditJour;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

/// 操作审计
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AuditJourRepository extends HBeanRepository<Long, AuditJour> {

    public AuditJourRepository(Database db) {
        super(AuditJour.class, db);
    }

    /// 分页查询
    ///
    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | create_time | date-time |
    /// | user_id | int |
    /// | ip_addr | string |
    public PagedList<AuditJour> findPage(DataFilter dataFilter) {
        var qb = new QAuditJour(db());
        var rsqlCtx = RSQLContext.of(qb)
                .property("create_time", qb.createTime)
                .property("user_id", qb.userId)
                .property("ip_addr", qb.ipAddr);
        super.apply(dataFilter, rsqlCtx);
        return qb.findPagedList();
    }
}

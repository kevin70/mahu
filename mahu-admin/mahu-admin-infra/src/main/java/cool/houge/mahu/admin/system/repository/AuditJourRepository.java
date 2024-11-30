package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
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
    public PagedList<AuditJour> findPage(DataFilter dataFilter) {
        var qb = new QAuditJour(db());
        apply(qb.query(), dataFilter);
        return qb.findPagedList();
    }
}

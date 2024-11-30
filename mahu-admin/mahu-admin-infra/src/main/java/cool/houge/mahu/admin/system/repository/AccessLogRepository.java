package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
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

    public PagedList<AccessLog> findPage(DataFilter dataFilter) {
        var qb = new QAccessLog(db());
        apply(qb.query(), dataFilter);
        return qb.findPagedList();
    }
}

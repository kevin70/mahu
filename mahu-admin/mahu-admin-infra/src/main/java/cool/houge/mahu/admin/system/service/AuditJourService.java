package cool.houge.mahu.admin.system.service;

import cool.houge.mahu.admin.system.repository.AuditJourRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.system.AuditJour;
import io.ebean.PagedList;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 操作审计
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AuditJourService {

    @Inject
    AuditJourRepository auditJourRepository;

    /// 分页查询
    public PagedList<AuditJour> findPage(DataFilter dataFilter) {
        return auditJourRepository.findPage(dataFilter);
    }
}

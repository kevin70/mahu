package cool.houge.mahu.admin.system.service;

import cool.houge.mahu.admin.entity.AdminAuditLog;
import cool.houge.mahu.admin.shared.SharedToolService;
import cool.houge.mahu.admin.system.repository.AuditJourRepository;
import cool.houge.mahu.common.DataFilter;
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

    @Inject
    SharedToolService toolService;

    /// 分页查询
    public PagedList<AdminAuditLog> findPage(DataFilter dataFilter) {
        var plist = auditJourRepository.findPage(dataFilter);
        return toolService.wrap(plist, dataFilter);
    }
}

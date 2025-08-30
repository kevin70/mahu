package cool.houge.mahu.admin.service;

import cool.houge.mahu.admin.entity.AdminAccessLog;
import cool.houge.mahu.admin.entity.AdminAuditLog;
import cool.houge.mahu.admin.entity.AdminAuthLog;
import cool.houge.mahu.admin.repository.AdminAccessLogRepository;
import cool.houge.mahu.admin.repository.AdminAuditLogRepository;
import cool.houge.mahu.admin.repository.AdminAuthLogRepository;
import cool.houge.mahu.domain.DataFilter;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service.Singleton;
import lombok.AllArgsConstructor;

/// 业务日志模块
///
/// @author ZY (kzou227@qq.com)
@AllArgsConstructor
@Singleton
public class LogService {

    final AdminAuthLogRepository adminAuthLogRepository;
    final AdminAccessLogRepository adminAccessLogRepository;
    final AdminAuditLogRepository adminAuditLogRepository;

    /// 管理员后台登录记录
    @Transactional(readOnly = true)
    public PagedList<AdminAuthLog> findPage4AdminAuthLog(DataFilter dataFilter) {
        return adminAuthLogRepository.findPage(dataFilter);
    }

    /// 管理员后台访问记录
    @Transactional(readOnly = true)
    public PagedList<AdminAccessLog> findPage4AdminAccessLog(DataFilter dataFilter) {
        return adminAccessLogRepository.findPage(dataFilter);
    }

    /// 管理员操作审计日志
    @Transactional(readOnly = true)
    public PagedList<AdminAuditLog> findPage4AdminAuditLog(DataFilter dataFilter) {
        return adminAuditLogRepository.findPage(dataFilter);
    }
}

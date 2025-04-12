package cool.houge.mahu.admin.service;

import cool.houge.mahu.admin.entity.AdminAccessLog;
import cool.houge.mahu.admin.entity.AdminAuditLog;
import cool.houge.mahu.admin.entity.AdminAuthLog;
import cool.houge.mahu.admin.repository.LogRepository;
import cool.houge.mahu.common.DataFilter;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 业务日志模块
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class LogService {

    @Inject
    LogRepository logRepository;

    /// 管理员后台登录记录
    @Transactional(readOnly = true)
    public PagedList<AdminAuthLog> findPage4AdminAuthLog(DataFilter dataFilter) {
        return logRepository.findPage4AdminAuthLog(dataFilter);
    }

    /// 管理员后台访问记录
    @Transactional(readOnly = true)
    public PagedList<AdminAccessLog> findPage4AdminAccessLog(DataFilter dataFilter) {
        return logRepository.findPage4AdminAccessLog(dataFilter);
    }

    /// 管理员操作审计日志
    @Transactional(readOnly = true)
    public PagedList<AdminAuditLog> findPage4AdminAuditLog(DataFilter dataFilter) {
        return logRepository.findPage4AdminAuditLog(dataFilter);
    }
}

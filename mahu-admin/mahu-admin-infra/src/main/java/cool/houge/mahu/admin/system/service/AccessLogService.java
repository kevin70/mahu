package cool.houge.mahu.admin.system.service;

import cool.houge.mahu.admin.shared.SharedToolService;
import cool.houge.mahu.admin.system.repository.AccessLogRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.system.AccessLog;
import io.avaje.inject.events.Observes;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 后台访问日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AccessLogService {

    @Inject
    AccessLogRepository accessLogRepository;

    @Inject
    SharedToolService toolService;

    @Transactional
    void handleAccessLog(@Observes AccessLog accessLog) {
        accessLogRepository.save(accessLog);
    }

    /// 分页查询访问日志
    @Transactional(readOnly = true)
    public PagedList<AccessLog> findPage(DataFilter dataFilter) {
        var plist = accessLogRepository.findPage(dataFilter);
        return toolService.wrap(plist, dataFilter);
    }
}

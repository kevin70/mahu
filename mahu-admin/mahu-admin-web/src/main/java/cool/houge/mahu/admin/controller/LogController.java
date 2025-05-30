package cool.houge.mahu.admin.controller;

import static cool.houge.mahu.admin.Permits.ADMIN_ACCESS_LOG;
import static cool.houge.mahu.admin.Permits.ADMIN_AUDIT_LOG;
import static cool.houge.mahu.admin.Permits.ADMIN_AUTH_LOG;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.service.LogService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 业务日志控制器
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class LogController implements HttpService, WebSupport {

    private final VoBeanMapper beanMapper;
    private final LogService logService;

    @Inject
    public LogController(VoBeanMapper beanMapper, LogService logService) {
        this.beanMapper = beanMapper;
        this.logService = logService;
    }

    @Override
    public void routing(HttpRules rules) {
        rules.get("/logs/admin-auth", s(this::listAdminAuthLogs, ADMIN_AUTH_LOG.R));
        rules.get("/logs/admin-audit", s(this::listAdminAuditLogs, ADMIN_AUDIT_LOG.R));
        rules.get("/logs/admin-access", s(this::listAdminAccessLogs, ADMIN_ACCESS_LOG.R));
    }

    private void listAdminAuthLogs(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = logService.findPage4AdminAuthLog(dataFilter);
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toAdminAuthLogResponse);
        response.send(rs);
    }

    private void listAdminAuditLogs(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = logService.findPage4AdminAuditLog(dataFilter);
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toAdminAuditLogResponse);
        response.send(rs);
    }

    private void listAdminAccessLogs(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = logService.findPage4AdminAccessLog(dataFilter);
        var rs =
                beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toAdminAccessLogResponse);
        response.send(rs);
    }
}

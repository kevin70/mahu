package cool.houge.mahu.admin.controller;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.service.LogService;
import cool.houge.mahu.common.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static cool.houge.mahu.admin.Permits.*;

/// 业务日志控制器
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class LogController implements HttpService, WebSupport {

    @Inject
    LogService logService;

    @Inject
    VoBeanMapper beanMapper;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/logs/admin-auth-logs", authz(ADMIN_AUTH_LOG.R()).wrap(this::listAdminAuthLogs));
        rules.get("/logs/admin-audit-logs", authz(ADMIN_AUDIT_LOG.R()).wrap(this::listAdminAuditLogs));
        rules.get("/logs/admin-access-logs", authz(ADMIN_ACCESS_LOG.R()).wrap(this::listAdminAccessLogs));
    }

    private void listAdminAuthLogs(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = logService.findPage4AdminAuthLog(dataFilter);
        var rs = beanMapper.toPageResponse(
                plist.getList(), plist.getTotalCount(), beanMapper::toAdminAuthLogResponse);
        response.send(rs);
    }

    private void listAdminAuditLogs(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = logService.findPage4AdminAuditLog(dataFilter);
        var rs = beanMapper.toPageResponse(
            plist.getList(), plist.getTotalCount(), beanMapper::toAdminAuditLogResponse);
        response.send(rs);
    }

    private void listAdminAccessLogs(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = logService.findPage4AdminAccessLog(dataFilter);
        var rs = beanMapper.toPageResponse(
                plist.getList(), plist.getTotalCount(), beanMapper::toAdminAccessLogResponse);
        response.send(rs);
    }
}

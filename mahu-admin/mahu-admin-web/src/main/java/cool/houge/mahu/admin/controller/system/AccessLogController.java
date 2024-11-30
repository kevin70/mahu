package cool.houge.mahu.admin.controller.system;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.system.service.AccessLogService;
import cool.houge.mahu.common.web.WebDataFilter;
import cool.houge.mahu.common.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static cool.houge.mahu.admin.Permits.ACCESS_LOG;

/// 访问日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AccessLogController implements HttpService, WebSupport {

    @Inject
    AccessLogService accessLogService;

    @Inject
    VoBeanMapper beanMapper;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/system/access-logs", authz(ACCESS_LOG.R()).wrap(this::listAccessLogs));
    }

    private void listAccessLogs(ServerRequest request, ServerResponse response) {
        var plist = accessLogService.findPage(new WebDataFilter(request));
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toGetAccessLogResponse);
        response.send(rs);
    }
}

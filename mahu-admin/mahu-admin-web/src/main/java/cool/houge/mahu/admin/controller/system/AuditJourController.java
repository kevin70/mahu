package cool.houge.mahu.admin.controller.system;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.system.service.AuditJourService;
import cool.houge.mahu.common.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static cool.houge.mahu.admin.Permits.AUDIT_JOUR;

/// 操作审计
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AuditJourController implements HttpService, WebSupport {

    @Inject
    AuditJourService auditJourService;

    @Inject
    VoBeanMapper beanMapper;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/system/audit-jours", authz(AUDIT_JOUR.R()).wrap(this::listAuditJours));
    }

    private void listAuditJours(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = auditJourService.findPage(dataFilter);
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toGetAuditJourResponse);
        response.send(rs);
    }
}

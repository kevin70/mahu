package cool.houge.mahu.admin.controller.system;

import cool.houge.mahu.admin.Permits;
import cool.houge.mahu.admin.oas.model.PermitResponse;
import cool.houge.mahu.common.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Singleton;

import java.util.ArrayList;

/// 权限
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class PermitController implements HttpService, WebSupport {

    @Override
    public void routing(HttpRules rules) {
        rules.get("/system/permits", authz().wrap(this::allPermits));
    }

    private void allPermits(ServerRequest request, ServerResponse response) {
        var list = new ArrayList<PermitResponse>();
        for (Permits value : Permits.values()) {
            if (value.canRead()) {
                list.add(new PermitResponse().setCode(value.R()).setLabel(value.label() + "|读取"));
            }
            if (value.canWrite()) {
                list.add(new PermitResponse().setCode(value.W()).setLabel(value.label() + "|写入"));
            }
            if (value.canDelete()) {
                list.add(new PermitResponse().setCode(value.D()).setLabel(value.label() + "|删除"));
            }
        }
        response.send(list);
    }
}

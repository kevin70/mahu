package cool.houge.mahu.admin.controller.system;

import cool.houge.mahu.admin.Permissions;
import cool.houge.mahu.admin.oas.model.PermitResponse;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.util.ArrayList;

/// 权限
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class PermissionController implements WebSupport {

    @Override
    public void routing(HttpRules rules) {
        rules.get("/system/permissions", s(this::allSystemPermissions));
    }

    private void allSystemPermissions(ServerRequest request, ServerResponse response) {
        var list = new ArrayList<PermitResponse>();
        for (Permissions value : Permissions.values()) {
            var o = new PermitResponse()
                    .setCode(value.name())
                    .setLabel(value.label())
                    .setCanRead(value.canRead())
                    .setCanWrite(value.canWrite())
                    .setCanDelete(value.canDelete());
            list.add(o);
        }
        response.send(list);
    }
}

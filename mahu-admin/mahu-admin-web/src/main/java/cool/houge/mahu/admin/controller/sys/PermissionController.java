package cool.houge.mahu.admin.controller.sys;

import cool.houge.mahu.admin.Permissions;
import cool.houge.mahu.admin.oas.controller.HPermissionService;
import cool.houge.mahu.admin.oas.vo.SysPermissionResponse;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.util.ArrayList;

/// 权限
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class PermissionController implements HPermissionService, WebSupport {

    @Override
    public void listSysPermission(ServerRequest request, ServerResponse response) {
        var list = new ArrayList<SysPermissionResponse>();
        for (Permissions value : Permissions.values()) {
            var o = new SysPermissionResponse()
                    .setCode(value.name())
                    .setModule(value.module())
                    .setLabel(value.label())
                    .setCanRead(value.canRead())
                    .setCanWrite(value.canWrite())
                    .setCanDelete(value.canDelete());
            list.add(o);
        }
        response.send(list);
    }
}

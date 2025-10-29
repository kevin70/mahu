package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.16.0", trigger = "openapi-generator")
public interface HPermissionService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.get("/sys/permissions", authenticate(), this::listSysPermission);
    }

    ///
    /// `GET /sys/permissions` 系统所有权限代码
    ///
    /// @param request the server request
    /// @param response the server response
    void listSysPermission(ServerRequest request, ServerResponse response);

}

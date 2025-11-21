package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.16.0", trigger = "openapi-generator")
public interface HFeatureService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.get("/sys/features", authenticate().andAuthorize("SYS_FEATURE:R"), this::pageSysFeature);
    }

    ///
    /// `GET /sys/features` 功能分页列表
    ///
    /// @param request the server request
    /// @param response the server response
    void pageSysFeature(ServerRequest request, ServerResponse response);

}

package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.20.0", trigger = "openapi-generator")
public interface HPublicFeatureFlagService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.get("/p/feature-flags",this::listPublicFeatureFlags);
    }

    ///
    /// `GET /p/feature-flags` 功能开关列表（公共）
    ///
    /// @param request the server request
    /// @param response the server response
    void listPublicFeatureFlags(ServerRequest request, ServerResponse response);

}

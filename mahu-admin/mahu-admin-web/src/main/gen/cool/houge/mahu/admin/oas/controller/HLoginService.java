package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.20.0", trigger = "openapi-generator")
public interface HLoginService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.post("/login/token",this::login);
    }

    ///
    /// `POST /login/token` 获取访问令牌
    ///
    /// @param request the server request
    /// @param response the server response
    void login(ServerRequest request, ServerResponse response);

}

package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.20.0", trigger = "openapi-generator")
public interface HHelpService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.get("/info",this::getInfo);
        rules.get("/samples", authenticate(), this::getSamples);
    }

    ///
    /// `GET /info` 获取应用信息
    ///
    /// @param request the server request
    /// @param response the server response
    void getInfo(ServerRequest request, ServerResponse response);

    ///
    /// `GET /samples` 样例
    ///
    /// @param request the server request
    /// @param response the server response
    void getSamples(ServerRequest request, ServerResponse response);

}

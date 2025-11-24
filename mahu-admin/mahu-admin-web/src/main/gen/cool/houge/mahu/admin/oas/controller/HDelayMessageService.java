package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.16.0", trigger = "openapi-generator")
public interface HDelayMessageService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.get("/sys/delay-messages", authenticate().andAuthorize("SYS_DELAY_MESSAGE:R"), this::pageSysDelayMessages);
    }

    ///
    /// `GET /sys/delay-messages` 延迟消息分页列表
    ///
    /// @param request the server request
    /// @param response the server response
    void pageSysDelayMessages(ServerRequest request, ServerResponse response);

}

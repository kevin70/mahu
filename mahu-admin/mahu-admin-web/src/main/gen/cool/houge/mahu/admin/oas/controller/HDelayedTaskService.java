package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.16.0", trigger = "openapi-generator")
public interface HDelayedTaskService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.get("/sys/delayed-tasks", authenticate().andAuthorize("SYS_DELAYED_TASK:R"), this::pageSysDelayedTask);
    }

    ///
    /// `GET /sys/delayed-tasks` 延迟任务分页列表
    ///
    /// @param request the server request
    /// @param response the server response
    void pageSysDelayedTask(ServerRequest request, ServerResponse response);

}

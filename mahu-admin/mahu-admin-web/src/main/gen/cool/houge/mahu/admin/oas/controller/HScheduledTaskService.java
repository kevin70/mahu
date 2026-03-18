package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.20.0", trigger = "openapi-generator")
public interface HScheduledTaskService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.delete("/sys/scheduled-tasks/{task_name}", authenticate().andAuthorize("SYS_SCHEDULED_TASK:W"), this::cancelSysScheduledTask);
        rules.post("/sys/scheduled-tasks/{task_name}", authenticate().andAuthorize("SYS_SCHEDULED_TASK:W"), this::executeSysScheduledTask);
        rules.get("/sys/scheduled-tasks", authenticate().andAuthorize("SYS_SCHEDULED_TASK:R"), this::pageSysScheduledTask);
        rules.get("/sys/scheduled-tasks/{task_name}/logs", authenticate().andAuthorize("SYS_SCHEDULED_TASK:R"), this::pageSysScheduledTaskLog);
    }

    ///
    /// `DELETE /sys/scheduled-tasks/{task_name}` 取消执行任务
    ///
    /// @param request the server request
    /// @param response the server response
    void cancelSysScheduledTask(ServerRequest request, ServerResponse response);

    ///
    /// `POST /sys/scheduled-tasks/{task_name}` 立即执行任务
    ///
    /// @param request the server request
    /// @param response the server response
    void executeSysScheduledTask(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/scheduled-tasks` 定时任务分页列表
    ///
    /// @param request the server request
    /// @param response the server response
    void pageSysScheduledTask(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/scheduled-tasks/{task_name}/logs` 定时任务执行记录
    ///
    /// @param request the server request
    /// @param response the server response
    void pageSysScheduledTaskLog(ServerRequest request, ServerResponse response);

}

package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.20.0", trigger = "openapi-generator")
public interface HAdminService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.post("/sys/admins", authenticate().andAuthorize("SYS_ADMIN:W"), this::createSysAdmin);
        rules.delete("/sys/admins/{admin_id}", authenticate().andAuthorize("SYS_ADMIN:W"), this::deleteSysAdmin);
        rules.get("/sys/admins/{admin_id}", authenticate().andAuthorize("SYS_ADMIN:R"), this::getSysAdmin);
        rules.get("/sys/admins", authenticate().andAuthorize("SYS_ADMIN:R"), this::pageSysAdmin);
        rules.get("/sys/admin-logs/{type}", authenticate().andAuthorize("SYS_ADMIN_LOG:R"), this::pageSysAdminLog);
        rules.put("/sys/admins/{admin_id}", authenticate().andAuthorize("SYS_ADMIN:W"), this::updateSysAdmin);
    }

    ///
    /// `POST /sys/admins` 新建管理员
    ///
    /// @param request the server request
    /// @param response the server response
    void createSysAdmin(ServerRequest request, ServerResponse response);

    ///
    /// `DELETE /sys/admins/{admin_id}` 删除管理员
    ///
    /// @param request the server request
    /// @param response the server response
    void deleteSysAdmin(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/admins/{admin_id}` 获取指定管理员
    ///
    /// @param request the server request
    /// @param response the server response
    void getSysAdmin(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/admins` 管理员分页列表
    ///
    /// @param request the server request
    /// @param response the server response
    void pageSysAdmin(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/admin-logs/{type}` 管理员日志
    ///
    /// @param request the server request
    /// @param response the server response
    void pageSysAdminLog(ServerRequest request, ServerResponse response);

    ///
    /// `PUT /sys/admins/{admin_id}` 修改管理员
    ///
    /// @param request the server request
    /// @param response the server response
    void updateSysAdmin(ServerRequest request, ServerResponse response);

}

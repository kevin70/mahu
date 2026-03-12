package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.20.0", trigger = "openapi-generator")
public interface HAuthClientService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.post("/sys/auth-clients", authenticate().andAuthorize("SYS_AUTH_CLIENT:W"), this::createSysAuthClient);
        rules.delete("/sys/auth-clients/{client_id}", authenticate().andAuthorize("SYS_AUTH_CLIENT:W"), this::deleteSysAuthClient);
        rules.get("/sys/auth-clients/{client_id}", authenticate().andAuthorize("SYS_AUTH_CLIENT:R"), this::getSysAuthClient);
        rules.get("/sys/auth-clients", authenticate().andAuthorize("SYS_AUTH_CLIENT:R"), this::pageSysAuthClient);
        rules.put("/sys/auth-clients/{client_id}", authenticate().andAuthorize("SYS_AUTH_CLIENT:W"), this::updateSysAuthClient);
    }

    ///
    /// `POST /sys/auth-clients` 新建认证终端
    ///
    /// @param request the server request
    /// @param response the server response
    void createSysAuthClient(ServerRequest request, ServerResponse response);

    ///
    /// `DELETE /sys/auth-clients/{client_id}` 删除认证终端
    ///
    /// @param request the server request
    /// @param response the server response
    void deleteSysAuthClient(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/auth-clients/{client_id}` 获取指定认证终端
    ///
    /// @param request the server request
    /// @param response the server response
    void getSysAuthClient(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/auth-clients` 认证终端分页列表
    ///
    /// @param request the server request
    /// @param response the server response
    void pageSysAuthClient(ServerRequest request, ServerResponse response);

    ///
    /// `PUT /sys/auth-clients/{client_id}` 修改认证终端
    ///
    /// @param request the server request
    /// @param response the server response
    void updateSysAuthClient(ServerRequest request, ServerResponse response);

}

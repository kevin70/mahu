package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.14.0", trigger = "openapi-generator")
public interface HRoleService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.post("/sys/roles", authenticate().andAuthorize("SYS_ROLE:W"), this::createSysRole);
        rules.delete("/sys/roles/{role_id}", authenticate().andAuthorize("SYS_ROLE:W"), this::deleteSysRole);
        rules.get("/sys/roles/{role_id}", authenticate().andAuthorize("SYS_ROLE:R"), this::getSysRole);
        rules.get("/sys/roles", authenticate().andAuthorize("SYS_ROLE:R"), this::pageSysRole);
        rules.put("/sys/roles/{role_id}", authenticate().andAuthorize("SYS_ROLE:W"), this::updateSysRole);
    }

    ///
    /// `POST /sys/roles` 新建角色
    ///
    /// @param request the server request
    /// @param response the server response
    void createSysRole(ServerRequest request, ServerResponse response);

    ///
    /// `DELETE /sys/roles/{role_id}` 删除角色
    ///
    /// @param request the server request
    /// @param response the server response
    void deleteSysRole(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/roles/{role_id}` 获取指定角色
    ///
    /// @param request the server request
    /// @param response the server response
    void getSysRole(ServerRequest request, ServerResponse response);

    ///
    /// `GET /sys/roles` 角色分页列表
    ///
    /// @param request the server request
    /// @param response the server response
    void pageSysRole(ServerRequest request, ServerResponse response);

    ///
    /// `PUT /sys/roles/{role_id}` 修改角色
    ///
    /// @param request the server request
    /// @param response the server response
    void updateSysRole(ServerRequest request, ServerResponse response);

}

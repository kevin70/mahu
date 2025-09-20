package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.14.0", trigger = "openapi-generator")
public interface HMeService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.get("/me/profile", authenticate(), this::getMeProfile);
        rules.put("/me/password", authenticate(), this::updateMePassword);
        rules.put("/me/profile", authenticate(), this::updateMeProfile);
    }

    ///
    /// `GET /me/profile` 获取个人登录信息
    ///
    /// @param request the server request
    /// @param response the server response
    void getMeProfile(ServerRequest request, ServerResponse response);

    ///
    /// `PUT /me/password` 修改密码
    ///
    /// @param request the server request
    /// @param response the server response
    void updateMePassword(ServerRequest request, ServerResponse response);

    ///
    /// `PUT /me/profile` 更新个人信息
    ///
    /// @param request the server request
    /// @param response the server response
    void updateMeProfile(ServerRequest request, ServerResponse response);

}

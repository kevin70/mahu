package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.20.0", trigger = "openapi-generator")
public interface HMeService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.get("/me/notifications/poll", authenticate(), this::getMeNotificationPoll);
        rules.get("/me/profile", authenticate(), this::getMeProfile);
        rules.get("/me/notifications", authenticate(), this::pageMeNotification);
        rules.post("/me/notifications/read-batch", authenticate(), this::updateBatchMeNotificationRead);
        rules.post("/me/notifications/{notification_id}/read", authenticate(), this::updateMeNotificationRead);
        rules.put("/me/password", authenticate(), this::updateMePassword);
        rules.put("/me/profile", authenticate(), this::updateMeProfile);
    }

    ///
    /// `GET /me/notifications/poll` 我的通知增量轮询
    ///
    /// @param request the server request
    /// @param response the server response
    void getMeNotificationPoll(ServerRequest request, ServerResponse response);

    ///
    /// `GET /me/profile` 获取个人登录信息
    ///
    /// @param request the server request
    /// @param response the server response
    void getMeProfile(ServerRequest request, ServerResponse response);

    ///
    /// `GET /me/notifications` 我的通知分页列表
    ///
    /// @param request the server request
    /// @param response the server response
    void pageMeNotification(ServerRequest request, ServerResponse response);

    ///
    /// `POST /me/notifications/read-batch` 批量标记我的通知为已读
    ///
    /// @param request the server request
    /// @param response the server response
    void updateBatchMeNotificationRead(ServerRequest request, ServerResponse response);

    ///
    /// `POST /me/notifications/{notification_id}/read` 标记我的通知为已读
    ///
    /// @param request the server request
    /// @param response the server response
    void updateMeNotificationRead(ServerRequest request, ServerResponse response);

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

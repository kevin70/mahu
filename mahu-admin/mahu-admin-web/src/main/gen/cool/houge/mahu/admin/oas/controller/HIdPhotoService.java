package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.20.0", trigger = "openapi-generator")
public interface HIdPhotoService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.post("/id-photos", authenticate(), this::createIdPhotoPresigned);
        rules.get("/id-photos/{id_photo_id}/forward", authenticate(), this::forwardIdPhoto);
    }

    ///
    /// `POST /id-photos` 证件照预签名上传
    ///
    /// @param request the server request
    /// @param response the server response
    void createIdPhotoPresigned(ServerRequest request, ServerResponse response);

    ///
    /// `GET /id-photos/{id_photo_id}/forward` 定向证件照
    ///
    /// @param request the server request
    /// @param response the server response
    void forwardIdPhoto(ServerRequest request, ServerResponse response);

}

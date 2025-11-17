package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.16.0", trigger = "openapi-generator")
public interface HFileService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.post("/files", authenticate(), this::createFilePresigned);
    }

    ///
    /// `POST /files` 文件预签名上传
    ///
    /// @param request the server request
    /// @param response the server response
    void createFilePresigned(ServerRequest request, ServerResponse response);

}

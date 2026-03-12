package cool.houge.mahu.admin.oas.controller;

import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import io.helidon.webserver.http.HttpService;
import static io.helidon.webserver.http.SecureHandler.*;

@io.helidon.common.Generated(value = "7.20.0", trigger = "openapi-generator")
public interface HPublicDictService extends HttpService {

    @Override
    default void routing(HttpRules rules) {
        rules.get("/p/dicts/{dc}",this::getPublicDict);
        rules.get("/p/dicts",this::listPublicDict);
    }

    ///
    /// `GET /p/dicts/{dc}` 获取字典数据
    ///
    /// @param request the server request
    /// @param response the server response
    void getPublicDict(ServerRequest request, ServerResponse response);

    ///
    /// `GET /p/dicts` 字典分组列表
    ///
    /// @param request the server request
    /// @param response the server response
    void listPublicDict(ServerRequest request, ServerResponse response);

}

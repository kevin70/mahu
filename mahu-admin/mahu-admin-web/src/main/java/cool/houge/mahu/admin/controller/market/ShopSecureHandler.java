package cool.houge.mahu.admin.controller.market;

import cool.houge.mahu.common.web.WebSupport;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

import java.util.Objects;

/// 商店权限校验
///
/// @author ZY (kzou227@qq.com)
public class ShopSecureHandler implements WebSupport, Handler {

    private final Handler directHandler;

    public ShopSecureHandler(Handler directHandler) {
        Objects.requireNonNull(directHandler);
        this.directHandler = directHandler;
    }

    @Override
    public void handle(ServerRequest request, ServerResponse response) throws Exception {
        var ac = authContext();
        var pathParams = request.path().pathParameters();
        var shopId = pathParams.first("shop_id").get();

        directHandler.handle(request, response);
    }
}

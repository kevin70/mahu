package cool.houge.mahu.admin.controller;

import cool.houge.mahu.admin.DynamicPermit;
import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.common.BizCodeException;
import cool.houge.mahu.common.BizCodes;
import io.helidon.webserver.http.Handler;

/// 动态安全校验
/// @author ZY (kzou227@qq.com)
public interface DynamicSecureHandler {

    /// 商店安全校验
    /// @param directHandler 校验成功执行处理器
    default Handler shopSecure(Handler directHandler) {
        return (request, response) -> {
            var ac = AuthContext.get();
            var path = request.path();
            var p = new DynamicPermit(DynamicPermit.KIND_SHOP, path.pathParameters());
            if (!ac.checkPermit(p)) {
                throw new BizCodeException(BizCodes.PERMISSION_DENIED, "没有商店访问权限");
            }
            directHandler.handle(request, response);
        };
    }
}

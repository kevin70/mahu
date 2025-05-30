package cool.houge.mahu.web;

import static io.helidon.webserver.http.SecureHandler.authenticate;

import com.google.common.base.Splitter;
import cool.houge.mahu.common.DataFilter;
import io.helidon.http.HeaderNames;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;

/// Web 支持接口
///
/// @author ZY (kzou227@qq.com)
public interface WebSupport {

    /// 校验对象
    ///
    /// @param o 校验的对象
    default void validate(Object o) {
        ValidatorHolder.VALIDATOR.validate(o);
    }

    /// 获取数据过滤对象
    ///
    /// @param request 请求对象
    default DataFilter dataFilter(ServerRequest request) {
        return new WebDataFilter(request);
    }

    /// 获取客户端请求 IP
    ///
    /// @param request 请求对象
    default String clientAddr(ServerRequest request) {
        return request.headers()
                .first(HeaderNames.X_FORWARDED_FOR)
                .map(s -> {
                    var splitter = Splitter.on(',').omitEmptyStrings().trimResults();
                    var ipList = splitter.splitToList(s);
                    return ipList.getFirst();
                })
                .orElseGet(() -> request.remotePeer().host());
    }

    /// 需要用户认证的接口
    ///
    /// @param next 认证成功执行
    default Handler s(Handler next) {
        return authenticate().wrap(next);
    }

    /// 需要用户认证与权限认证的接口
    ///
    /// @param next 认证成功执行
    /// @param permit 权限代码
    default Handler s(Handler next, String permit) {
        return authenticate().andAuthorize(permit).wrap(next);
    }

    /// 需要用户认证与权限认证的接口
    ///
    /// @param next 认证成功执行
    /// @param permits 权限代码
    default Handler s(Handler next, String[] permits) {
        return authenticate().andAuthorize(permits).wrap(next);
    }
}

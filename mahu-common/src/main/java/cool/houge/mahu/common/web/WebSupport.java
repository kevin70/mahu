package cool.houge.mahu.common.web;

import com.google.common.base.Splitter;
import cool.houge.mahu.common.DataFilter;
import io.helidon.http.HeaderNames;
import io.helidon.webserver.http.SecureHandler;
import io.helidon.webserver.http.ServerRequest;

import static io.helidon.webserver.http.SecureHandler.authenticate;

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

    /// @param permits 权限名称
    default SecureHandler authz(String... permits) {
        if (permits.length == 0) {
            return authenticate();
        }
        return authenticate().andAuthorize(permits);
    }
}

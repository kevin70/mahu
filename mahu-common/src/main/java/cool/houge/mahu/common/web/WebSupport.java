package cool.houge.mahu.common.web;

import com.google.common.base.Splitter;
import cool.houge.lang.BizCodeException;
import cool.houge.lang.BizCodes;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.security.AuthContext;
import io.avaje.validation.Validator;
import io.helidon.common.LazyValue;
import io.helidon.common.context.Contexts;
import io.helidon.http.HeaderNames;
import io.helidon.webserver.http.SecureHandler;
import io.helidon.webserver.http.ServerRequest;

import javax.annotation.Nonnull;

import static io.helidon.webserver.http.SecureHandler.authenticate;

/// Web 支持接口
///
/// @author ZY (kzou227@qq.com)
public interface WebSupport {

    LazyValue<Validator> VALIDATOR_LV =
        LazyValue.create(() -> Validator.builder().build());

    /// 校验对象
    ///
    /// @param o 校验的对象
    default void validate(Object o) {
        VALIDATOR_LV.get().validate(o);
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
    default String clientIp(ServerRequest request) {
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

    /// 获取认证上下文
    default @Nonnull AuthContext authContext() {
        return Contexts.context()
            .orElseThrow(() -> new BizCodeException(BizCodes.UNAVAILABLE, "没有发现 helidon 上下文对象"))
            .get(AuthContext.class)
            .orElseThrow(() -> new BizCodeException(BizCodes.UNAUTHENTICATED, "缺少认证上下文"));
    }

    /// 当前认证的用户 ID
    default long uid() {
        var ac = authContext();
        return ac.uid();
    }
}

package cool.houge.mahu.web;

import static io.helidon.webserver.http.SecureHandler.authenticate;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.util.DataFilter;
import cool.houge.mahu.util.Paging;
import io.helidon.common.mapper.OptionalValue;
import io.helidon.common.mapper.Value;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import java.util.function.Function;

/// Web 支持接口
///
/// @author ZY (kzou227@qq.com)
public interface WebSupport extends HttpService {

    /// {@inheritDoc}
    @Override
    void routing(HttpRules rules);

    /// 校验对象
    ///
    /// @param o 校验的对象
    default void validate(Object o) {
        ValidatorHolder.VALIDATOR.validate(o);
    }

    /// 获取路径参数
    ///
    /// @param request 请求对象
    /// @param name    参数名称
     default Value<String> pathArg(ServerRequest request, String name) {
        return ServerRequestUtils.pathArg(request, name);
    }

    /// 获取查询参数
    ///
    /// @param request 请求对象
    /// @param name    参数名称
    default OptionalValue<String> queryArg(ServerRequest request, String name) {
        return ServerRequestUtils.queryArg(request, name);
    }

    /// 获取请求分页参数
    ///
    /// @param request 请求对象
    default Paging paging(ServerRequest request) {
        return new WebDataFilter(request);
    }

    /// 获取数据过滤对象
    ///
    /// @param request 请求对象
    default DataFilter dataFilter(ServerRequest request) {
        return new WebDataFilter(request);
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
    /// @param p1 权限代码
    default Handler s(Handler next, String p1) {
        return authenticate().andAuthorize(p1).wrap(next);
    }

    /// 包装器
    default <R> Function<String, R> w(Function<String, R> next) {
        return s -> {
            try {
                return next.apply(s);
            } catch (IllegalArgumentException e) {
                throw new BizCodeException(BizCodes.INVALID_ARGUMENT, e.getMessage());
            }
        };
    }
}


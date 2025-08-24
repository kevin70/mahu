package cool.houge.mahu.web;

import io.helidon.common.mapper.OptionalValue;
import io.helidon.common.mapper.Value;
import io.helidon.common.parameters.Parameters;
import io.helidon.webserver.http.ServerRequest;
import lombok.experimental.UtilityClass;

/// [ServerRequest] 工具类
///
/// @author ZY (kzou227@qq.com)
@UtilityClass
public final class ServerRequestUtils {

    /// 获取路径参数
    ///
    /// @param request 请求对象
    /// @param name    参数名称
    public static Value<String> pathArg(ServerRequest request, String name) {
        return first(request.path().pathParameters(), name);
    }

    /// 获取查询参数
    ///
    /// @param request 请求对象
    /// @param name    参数名称
    public static OptionalValue<String> queryArg(ServerRequest request, String name) {
        return first(request.query(), name);
    }

    static OptionalValue<String> first(Parameters parameters, String name) {
        return new RequestParameterValue(parameters.first(name));
    }
}

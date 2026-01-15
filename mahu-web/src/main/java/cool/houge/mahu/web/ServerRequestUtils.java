package cool.houge.mahu.web;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import io.helidon.common.mapper.MapperException;
import io.helidon.common.mapper.OptionalValue;
import io.helidon.common.mapper.Value;
import io.helidon.webserver.http.ServerRequest;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;

/// [ServerRequest] 工具类
///
/// @author ZY (kzou227@qq.com)
@UtilityClass
public final class ServerRequestUtils {

    /// 从请求路径中获取布尔类型的参数值
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    public static boolean pathBoolean(ServerRequest request, String name) {
        try {
            return pathArg(request, name).asBoolean().get();
        } catch (MapperException e) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, e.getMessage());
        }
    }

    /// 从请求路径中获取整型的参数值
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    public static int pathInt(ServerRequest request, String name) {
        try {
            return pathArg(request, name).asInt().get();
        } catch (MapperException e) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, e.getMessage());
        }
    }

    /// 从请求路径中获取长整型的参数值
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    public static long pathLong(ServerRequest request, String name) {
        try {
            return pathArg(request, name).asLong().get();
        } catch (MapperException e) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, e.getMessage());
        }
    }

    /// 从请求路径中获取字符串类型的参数值
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    public static String pathString(ServerRequest request, String name) {
        return pathArg(request, name).get();
    }

    /// 获取路径参数
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    public static Value<String> pathArg(ServerRequest request, String name) {
        return request.path().pathParameters().first(name);
    }

    /// 从查询参数中获取布尔类型的参数值（可选）
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    public static Optional<Boolean> queryBoolean(ServerRequest request, String name) {
        try {
            return queryArg(request, name).asBoolean().asOptional();
        } catch (MapperException e) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, e.getMessage());
        }
    }

    /// 从查询参数中获取整型的参数值（可选）
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    public static Optional<Integer> queryInt(ServerRequest request, String name) {
        try {
            return queryArg(request, name).asInt().asOptional();
        } catch (MapperException e) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, e.getMessage());
        }
    }

    /// 从查询参数中获取长整型的参数值
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    public static long queryLong(ServerRequest request, String name) {
        try {
            return queryArg(request, name).asLong().get();
        } catch (MapperException e) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, e.getMessage());
        }
    }

    /// 获取查询参数
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    public static OptionalValue<String> queryArg(ServerRequest request, String name) {
        return request.query().first(name);
    }

    /// 从查询参数中获取整型列表的参数值
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    public static List<Integer> queryListInt(ServerRequest request, String name) {
        return request.query().allValues(name).stream()
                .map(v -> v.asInt().get())
                .toList();
    }
}

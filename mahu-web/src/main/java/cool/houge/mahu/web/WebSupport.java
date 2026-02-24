package cool.houge.mahu.web;

import com.google.common.primitives.Ints;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.domain.Sort;
import io.avaje.validation.Validator;
import io.helidon.common.mapper.MapperException;
import io.helidon.common.mapper.OptionalValue;
import io.helidon.common.mapper.Value;
import io.helidon.webserver.http.ServerRequest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/// Web 支持接口
///
/// @author ZY (kzou227@qq.com)
public interface WebSupport {

    /// 校验对象
    ///
    /// @param o 校验的对象
    default void validate(Object o) {
        Validator.instance().validate(o);
    }

    /// 获取分页参数对象
    ///
    /// @param request 请求对象
    default Page page(ServerRequest request) {
        return Page.of(request.query());
    }

    /// 获取排序参数对象
    ///
    /// @param request 请求对象
    default Sort sort(ServerRequest request) {
        return Sort.of(request.query());
    }

    /// 从请求路径中获取布尔类型的参数值
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    default boolean pathBoolean(ServerRequest request, String name) {
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
    default int pathInt(ServerRequest request, String name) {
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
    default long pathLong(ServerRequest request, String name) {
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
    default String pathString(ServerRequest request, String name) {
        return pathArg(request, name).get();
    }

    /// 获取路径参数
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    default Value<String> pathArg(ServerRequest request, String name) {
        return request.path().pathParameters().first(name);
    }

    /// 从查询参数中获取布尔类型的参数值（可选）
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    default OptionalValue<Boolean> queryBoolean(ServerRequest request, String name) {
        try {
            return queryArg(request, name).asBoolean();
        } catch (MapperException e) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, e.getMessage());
        }
    }

    /// 从查询参数中获取整型的参数值（可选）
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    default OptionalValue<Integer> queryInt(ServerRequest request, String name) {
        try {
            return queryArg(request, name).asInt();
        } catch (MapperException e) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, e.getMessage());
        }
    }

    /// 从查询参数中获取长整型的参数值
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    default OptionalValue<Long> queryLong(ServerRequest request, String name) {
        try {
            return queryArg(request, name).asLong();
        } catch (MapperException e) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, e.getMessage());
        }
    }

    /// 获取查询参数
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    default OptionalValue<String> queryArg(ServerRequest request, String name) {
        return request.query().first(name).as(String::trim);
    }

    /// 从查询参数中获取整型列表的参数值
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    default Optional<List<Integer>> queryIntArgs(ServerRequest request, String name) {
        return queryArgs(request, name)
                .map(o -> o.stream()
                        .map(String::trim)
                        .map(Ints::tryParse)
                        .filter(Objects::nonNull)
                        .toList());
    }

    /// 从查询参数中获取列表的参数值
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    default Optional<List<String>> queryArgs(ServerRequest request, String name) {
        var q = request.query();
        if (!q.contains(name)) {
            return Optional.empty();
        }
        return Optional.of(q.all(name));
    }
}

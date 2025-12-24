package cool.houge.mahu.web;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.domain.PageRequest;
import cool.houge.mahu.domain.Pageable;
import cool.houge.mahu.domain.Sort;
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

    /// 从请求中获取分页参数并构建 Pageable 对象
    ///
    /// @param request 请求对象
    /// @return 分页参数对象
    /// @throws BizCodeException 如果缺少 `page` 或 `size` 参数
    public static Pageable queryPage(ServerRequest request) {
        var p = pageArgs(request);
        if (p.isUnpaged()) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "缺少`page`或`size`分页参数");
        }
        return p;
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

    /// 获取分页参数对象
    ///
    /// @param request 请求对象
    public static Pageable pageArgs(ServerRequest request) {
        var sort = sortArgs(request);
        var pageSize = queryInt(request, "size");
        if (pageSize.isEmpty()) {
            return Pageable.unpaged(sort);
        }

        var page = queryInt(request, "page");
        return page.map(integer -> PageRequest.of(integer, pageSize.get(), sort))
                .orElseGet(() -> PageRequest.of(0, pageSize.get(), sort));
    }

    /// 获取排序参数
    ///
    /// @param request 请求对象
    public static Sort sortArgs(ServerRequest request) {
        var params = request.query();
        return toSort(params.all("sort", List::of));
    }

    /// 将排序参数转换为 Sort 对象
    ///
    /// @param sortParams 排序参数列表
    private static Sort toSort(List<String> sortParams) {
        if (sortParams.isEmpty()) {
            return Sort.unsorted();
        }

        var builder = Sort.builder();
        for (String s : sortParams) {
            if (s == null || s.isEmpty()) {
                continue;
            }

            boolean ascending = s.charAt(0) != '-';
            String paramName = ascending ? s : s.substring(1);
            if (ascending) {
                builder.asc(paramName);
            } else {
                builder.desc(paramName);
            }
        }
        return builder.build();
    }
}

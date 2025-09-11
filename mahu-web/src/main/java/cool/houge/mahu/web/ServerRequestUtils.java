package cool.houge.mahu.web;

import cool.houge.mahu.domain.PageRequest;
import cool.houge.mahu.domain.Pageable;
import cool.houge.mahu.domain.Sort;
import io.helidon.common.mapper.OptionalValue;
import io.helidon.common.mapper.Value;
import io.helidon.common.parameters.Parameters;
import io.helidon.webserver.http.ServerRequest;
import java.util.List;
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

    /// 获取可分页参数对象
    ///
    /// @param request 请求对象
    public static Pageable pageArgs(ServerRequest request) {
        var params = request.query();
        var sort = toSort(params.all("sort", List::of));
        var pageSize = params.first("size");
        if (pageSize.isEmpty()) {
            return Pageable.unpaged(sort);
        }

        var page = params.first("page");
        if (page.isEmpty()) {
            return PageRequest.of(0, pageSize.asInt().get(), sort);
        }
        return PageRequest.of(page.asInt().get(), pageSize.asInt().get(), sort);
    }

    static OptionalValue<String> first(Parameters parameters, String name) {
        return new RequestParameterValue(parameters.first(name));
    }

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
            String name = ascending ? s : s.substring(1);
            if (ascending) {
                builder.asc(name);
            } else {
                builder.desc(name);
            }
        }
        return builder.build();
    }
}

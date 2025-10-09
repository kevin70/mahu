package cool.houge.mahu.web;

import cool.houge.mahu.domain.DataFilter;
import cool.houge.mahu.domain.Pageable;
import cool.houge.mahu.domain.Sort;
import io.avaje.validation.Validator;
import io.helidon.common.mapper.OptionalValue;
import io.helidon.common.mapper.Value;
import io.helidon.webserver.http.ServerRequest;

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

    /// 获取数据过滤对象
    ///
    /// @param request 请求对象
    default DataFilter dataFilter(ServerRequest request) {
        return new WebDataFilter(request);
    }

    /// 获取请求分页参数
    ///
    /// @param request 请求对象
    default Pageable page(ServerRequest request) {
        return ServerRequestUtils.pageArgs(request);
    }

    /// 获取过滤参数
    ///
    /// @param request 请求对象
    default String filter(ServerRequest request) {
        return queryArg(request, "filter").orElse(null);
    }

    /// 获取排序参数
    ///
    /// @param request 请求对象
    default Sort sort(ServerRequest request) {
        return ServerRequestUtils.sortArgs(request);
    }
}

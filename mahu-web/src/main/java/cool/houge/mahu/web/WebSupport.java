package cool.houge.mahu.web;

import cool.houge.mahu.domain.DataFilter;
import io.avaje.validation.Validator;
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

    /// 获取数据过滤对象
    ///
    /// @param request 请求对象
    default DataFilter dataFilter(ServerRequest request) {
        return new WebDataFilter(request);
    }
}

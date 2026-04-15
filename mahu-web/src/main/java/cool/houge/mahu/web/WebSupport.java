package cool.houge.mahu.web;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.domain.DateRange;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.domain.Sort;
import cool.houge.mahu.util.Metadata;
import io.avaje.validation.Validator;
import io.helidon.common.mapper.MapperException;
import io.helidon.common.mapper.OptionalValue;
import io.helidon.common.mapper.Value;
import io.helidon.webserver.http.ServerRequest;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

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

    /// 获取接口版本号（从请求上下文中的 Metadata 读取）。
    default int apiVersion(ServerRequest request) {
        return request.context().get(Metadata.class).map(Metadata::apiVersion).orElse(1);
    }

    /// 从查询参数中获取日期区间（可选）。
    ///
    /// 默认使用参数名 "start_date" 与 "end_date"。
    default @NonNull DateRange dateRange(ServerRequest request) {
        return dateRange(request, "start_date", "end_date");
    }

    /// 从查询参数中获取日期区间（可选）。
    ///
    /// 当两个参数都不存在时返回 [DateRange#EMPTY]；
    /// 若仅提供一个参数，则表示仅起始或仅结束的开区间；
    /// 若日期格式非法，则抛出 INVALID_ARGUMENT。
    ///
    /// @param request 请求对象
    /// @param startParam 开始日期参数名
    /// @param endParam 结束日期参数名
    default @NonNull DateRange dateRange(ServerRequest request, String startParam, String endParam) {
        var startOpt = queryArg(request, startParam);
        var endOpt = queryArg(request, endParam);
        try {
            String start = startOpt.orElse(null);
            String end = endOpt.orElse(null);
            if (start == null && end == null) {
                return DateRange.EMPTY;
            }
            return DateRange.ofNullable(start, end);
        } catch (IllegalArgumentException e) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, e.getMessage());
        }
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

    /// 从查询参数中获取长整型的参数值（可选）
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

    /// 从查询参数中获取整型列表的参数值（可选）。
    /// 若任一值无法解析为整数则抛出 INVALID_ARGUMENT。
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    default Optional<List<Integer>> queryIntList(ServerRequest request, String name) {
        return queryArgs(request, name).map(o -> parseIntList(name, o));
    }

    /// 从查询参数中获取长整型列表的参数值（可选）。
    /// 若任一值无法解析为长整型则抛出 INVALID_ARGUMENT。
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    default Optional<List<Long>> queryLongList(ServerRequest request, String name) {
        return queryArgs(request, name).map(o -> parseLongList(name, o));
    }

    private static List<Integer> parseIntList(String paramName, List<String> raw) {
        var list = new java.util.ArrayList<Integer>(raw.size());
        for (var s : raw) {
            if (s == null || s.isEmpty()) {
                continue;
            }
            try {
                list.add(Integer.parseInt(s));
            } catch (NumberFormatException _) {
                throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "参数 " + paramName + " 含非法整数值: " + s);
            }
        }
        return list;
    }

    private static List<Long> parseLongList(String paramName, List<String> raw) {
        var list = new java.util.ArrayList<Long>(raw.size());
        for (var s : raw) {
            if (s == null || s.isEmpty()) {
                continue;
            }
            try {
                list.add(Long.parseLong(s));
            } catch (NumberFormatException _) {
                throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "参数 " + paramName + " 含非法长整型值: " + s);
            }
        }
        return list;
    }

    /// 从查询参数中获取列表的参数值（各元素已去除前后空格）
    ///
    /// @param request 请求对象
    /// @param name 参数名称
    default Optional<List<String>> queryArgs(ServerRequest request, String name) {
        var q = request.query();
        if (!q.contains(name)) {
            return Optional.empty();
        }
        return Optional.of(q.all(name).stream().map(String::trim).toList());
    }
}

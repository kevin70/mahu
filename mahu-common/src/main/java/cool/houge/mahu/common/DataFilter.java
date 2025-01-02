package cool.houge.mahu.common;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/// 数据过滤
///
/// @author ZY (kzou227@qq.com)
public interface DataFilter {

    /// 查询默认返回的最大行数
    int DEFAULT_LIMIT = 20;

    /// 此查询要返回的第一行
    int offset();

    /// 查询中要返回的最大行数
    int limit();

    /// 返回排序的对象
    @NonNull
    List<Sort> sorts();

    /// 返回数据过滤对象
    @NonNull
    List<Filter> filters();

    /// FIQL 数据过滤
    String filter();

    /// 返回是否包含软删除的数据
    default boolean isIncludeDeleted() {
        return false;
    }

    /// 是否不返回总记录数
    default boolean isNoTotalCount() {
        return false;
    }

    /// 排序
    ///
    /// @param name      排序的属性名称.
    /// @param direction 方向
    record Sort(String name, Direction direction) {}

    /// 排序方向
    enum Direction {
        /// 升序
        asc,
        /// 降序
        desc
    }

    /// 过滤
    ///
    /// @param name     属性名称
    /// @param op       操作符
    /// @param rawValue 无始值
    record Filter(String name, Op op, String rawValue) {

        /// 返回数组值
        public String[] values() {
            if (rawValue.isEmpty()) {
                return new String[0];
            }
            var v = URLDecoder.decode(rawValue, StandardCharsets.UTF_8);
            return v.split(",");
        }
    }

    /// 过滤操作符
    enum Op {
        none,
        eq,
        ne,
        gt,
        gte,
        lt,
        lte,
        contains,
        icontains,
        in,
        nin,
        between
    }
}

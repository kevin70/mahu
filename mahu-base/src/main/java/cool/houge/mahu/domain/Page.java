package cool.houge.mahu.domain;

import io.helidon.common.parameters.Parameters;
import org.jspecify.annotations.NonNull;

/// 分页信息的抽象接口。
///
/// @author ZY (kzou227@qq.com)
public interface Page {

    /// 默认页码
    int DEFAULT_PAGE = 1;

    /// 每页默认条目数
    int DEFAULT_PER_PAGE = 20;

    /// 判断当前分页对象是否不包含分页信息。
    ///
    /// @return 如果该对象没有分页信息，则返回 true；否则返回 false。
    boolean isUnpaged();

    /// 判断是否需要包含总记录数。
    ///
    /// @return 如果需要包含总记录数，则返回 true；否则返回 false。
    boolean isIncludeTotal();

    /// 获取要查询的页码。
    ///
    /// @return 要查询的页码。
    /// @throws UnsupportedOperationException 如果该对象是不分页的。
    int getPage();

    /// 获取每页要返回的条目数。
    ///
    /// @return 每页的条目数。
    /// @throws UnsupportedOperationException 如果该对象是不分页的。
    int getPerPage();

    /// 根据基础页码和页面大小计算并返回要获取的偏移量。
    ///
    /// @return 要获取的偏移量。
    /// @throws UnsupportedOperationException 如果该对象是不分页的。
    long getOffset();

    /// 获取排序参数。
    ///
    /// @return 排序参数。
    @NonNull
    Sort getSort();

    /// 创建一个表示无分页设置但具有指定排序的分页对象。
    ///
    /// @param sort 不能为 null 的排序参数，如果没有排序可以使用 [Sort#unsorted()]。
    /// @return 不会返回 null 的分页对象。
    static @NonNull Page unpaged(@NonNull Sort sort) {
        return new Unpaged(sort);
    }

    /// 使用给定的页码和每页条目数创建一个分页请求对象。
    ///
    /// @param page 要查询的页码。
    /// @param perPage 每页的条目数。
    /// @return 分页请求对象。
    static @NonNull Page of(int page, int perPage) {
        return new PageRequest(page, perPage, Sort.unsorted());
    }

    /// 根据传入的参数创建一个分页请求对象。
    ///
    /// 参数中可包含以下键值对：
    /// - `per_page`：每页要返回的条目数。
    /// - `page`：要查询的页码。
    /// - `include_total`：是否返回总记录数量（布尔值）。
    /// - `sort`：排序参数。
    ///
    /// @param params 包含分页参数的对象。
    /// @return 分页请求对象。
    static @NonNull Page of(@NonNull Parameters params) {
        return PageRequest.of(params);
    }
}

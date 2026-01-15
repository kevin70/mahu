package cool.houge.mahu.domain;

import static java.util.Objects.requireNonNullElse;

import com.google.common.primitives.Ints;
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

    /// 返回构建器
    static Builder builder() {
        return new Builder();
    }

    class Builder {

        private int page = DEFAULT_PAGE;
        private int perPage = DEFAULT_PER_PAGE;
        private boolean includeTotal = true;
        private Sort sort = Sort.unsorted();

        private Builder() {}

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder perPage(int perPage) {
            this.perPage = perPage;
            return this;
        }

        public Builder includeTotal(boolean includeTotal) {
            this.includeTotal = includeTotal;
            return this;
        }

        public Builder sort(@NonNull Sort sort) {
            this.sort = sort;
            return this;
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
        public Builder with(Parameters params) {
            this.sort = Sort.of(params);
            this.page = params.first("page")
                    .map(s -> requireNonNullElse(Ints.tryParse(s), DEFAULT_PAGE))
                    .orElse(DEFAULT_PAGE);
            this.perPage = params.first("per_page")
                    .map(s -> requireNonNullElse(Ints.tryParse(s), DEFAULT_PER_PAGE))
                    .orElse(DEFAULT_PER_PAGE);
            this.includeTotal = params.first("include_total").asBoolean().orElse(true);
            return this;
        }

        public Page build() {
            return new PageRequest(page, perPage, includeTotal, sort);
        }
    }
}

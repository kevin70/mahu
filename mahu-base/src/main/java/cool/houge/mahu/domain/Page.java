package cool.houge.mahu.domain;

import static java.util.Objects.requireNonNullElse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.primitives.Ints;
import io.helidon.common.parameters.Parameters;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

/// 分页信息的抽象接口。
///
/// @author ZY (kzou227@qq.com)
@Getter
@ToString
@EqualsAndHashCode
public class Page {

    /// 默认页码
    private static final int DEFAULT_PAGE = 1;
    /// 每页默认条目数
    private static final int DEFAULT_PER_PAGE = 20;

    /// 要查询的页码
    private final int page;
    /// 每页要返回的条目数
    private final int perPage;
    /// 是否返回总记录数量
    private final boolean includeTotal;
    /// 偏移量
    private final long offset;
    /// 排序参数
    private final Sort sort;

    private Page(int page, int perPage, boolean includeTotal, Sort sort) {
        if (page <= 0) {
            throw new IllegalArgumentException("页码必须为正数: " + page);
        }
        if (perPage <= 0) {
            throw new IllegalArgumentException("页大小必须为正数: " + perPage);
        }

        this.page = page;
        this.perPage = perPage;
        this.includeTotal = includeTotal;
        this.offset = (long) (page - 1) * perPage;
        this.sort = requireNonNullElse(sort, Sort.unsorted());
    }

    /// 获取当前排序对象中的所有排序项。
    ///
    /// @return 当前排序对象中的排序项列表。
    public List<Sort.Order> sortOrders() {
        return sort.getOrders();
    }

    /// 根据传入的参数创建一个分页请求对象。
    ///
    /// 参数中可包含以下键值对：
    /// - `page`：要查询的页码。
    /// - `per_page`：每页要返回的条目数。
    /// - `include_total`：是否返回总记录数量（布尔值）。
    /// - `sort`：排序参数。
    ///
    /// @param params 包含分页参数的对象。
    public static @NonNull Page of(@NonNull Parameters params) {
        var sort = Sort.of(params);
        var page = params.first("page")
            .map(s -> requireNonNullElse(Ints.tryParse(s), DEFAULT_PAGE))
            .orElse(DEFAULT_PAGE);
        var perPage = params.first("per_page")
            .map(s -> requireNonNullElse(Ints.tryParse(s), DEFAULT_PER_PAGE))
            .orElse(DEFAULT_PER_PAGE);
        var includeTotal = params.first("include_total").asBoolean().orElse(true);
        return new Page(page, perPage, includeTotal, sort);
    }

    /// 创建分页对象
    ///
    /// @param page 要查询的页码
    /// @param perPage 每页要返回的条目数
    /// @param includeTotal 是否返回总记录数量（布尔值）
    /// @param sort 排序参数
    /// @return 分页对象
    @JsonCreator
    public static @NonNull Page of(
            @JsonProperty("page") Integer page,
            @JsonProperty("per_page") Integer perPage,
            @JsonProperty("include_total") Boolean includeTotal,
            @JsonProperty("sort") Sort sort) {
        return new Page(
                requireNonNullElse(page, DEFAULT_PAGE),
                requireNonNullElse(perPage, DEFAULT_PER_PAGE),
                requireNonNullElse(includeTotal, true),
                requireNonNullElse(sort, Sort.unsorted())
                //
                );
    }

    /// 分页构建器
    public static Builder builder() {
        return new Builder();
    }

    /// 构建器
    public static class Builder {

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

        public Page build() {
            return new Page(page, perPage, includeTotal, sort);
        }
    }
}

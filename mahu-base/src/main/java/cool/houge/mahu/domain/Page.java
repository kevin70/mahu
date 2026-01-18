package cool.houge.mahu.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNullElse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.primitives.Ints;
import io.helidon.common.parameters.Parameters;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

/// 分页信息的抽象接口。
///
/// @author ZY (kzou227@qq.com)
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Page {

    /// 默认页码
    private static final int DEFAULT_PAGE = 1;
    /// 每页默认条目数
    private static final int DEFAULT_PAGE_SIZE = 20;

    /// 要查询的页码
    @lombok.Builder.Default
    private int page = DEFAULT_PAGE;
    /// 每页要返回的条目数
    @lombok.Builder.Default
    private int pageSize = DEFAULT_PAGE_SIZE;
    /// 是否返回总记录数量
    @lombok.Builder.Default
    private boolean includeTotal = true;
    /// 排序参数
    @lombok.Builder.Default
    private Sort sort = Sort.unsorted();

    private Page(int page, int pageSize, boolean includeTotal, Sort sort) {
        checkPage(page);
        checkPageSize(pageSize);

        this.page = page;
        this.pageSize = pageSize;
        this.includeTotal = includeTotal;
        this.sort = requireNonNullElse(sort, Sort.unsorted());
    }

    /// 获取当前排序对象中的所有排序项。
    ///
    /// @return 当前排序对象中的排序项列表。
    public List<Sort.Order> sortOrders() {
        return sort.getOrders();
    }

    /// 获取偏移量
    public long getOffset() {
        return (long) (page - 1) * pageSize;
    }

    /// 根据传入的参数创建一个分页请求对象。
    ///
    /// 参数中可包含以下键值对：
    /// - `page`：要查询的页码。
    /// - `page_size`：每页要返回的条目数。
    /// - `include_total`：是否返回总记录数量（布尔值）。
    /// - `sort`：排序参数。
    ///
    /// @param params 包含分页参数的对象。
    public static @NonNull Page of(@NonNull Parameters params) {
        var sort = Sort.of(params);
        var page = params.first("page").map(Ints::tryParse).orElse(DEFAULT_PAGE);
        var perPage = params.first("page_size").map(Ints::tryParse).orElse(DEFAULT_PAGE_SIZE);
        var includeTotal = params.first("include_total").asBoolean().orElse(true);
        return new Page(page, perPage, includeTotal, sort);
    }

    /// 创建分页对象
    ///
    /// @param page 要查询的页码
    /// @param pageSize 每页要返回的条目数
    /// @param includeTotal 是否返回总记录数量（布尔值）
    /// @param sort 排序参数
    /// @return 分页对象
    @JsonCreator
    public static @NonNull Page of(
            @JsonProperty("page") Integer page,
            @JsonProperty("page_size") Integer pageSize,
            @JsonProperty("include_total") Boolean includeTotal,
            @JsonProperty("sort") Sort sort) {
        return new Page(
                requireNonNullElse(page, DEFAULT_PAGE),
                requireNonNullElse(pageSize, DEFAULT_PAGE_SIZE),
                requireNonNullElse(includeTotal, true),
                requireNonNullElse(sort, Sort.unsorted())
                //
                );
    }

    private static void checkPage(int page) {
        checkArgument(page > 0, "页码必须为正数: %s", page);
    }

    private static void checkPageSize(int pageSize) {
        checkArgument(pageSize > 0, "每页条目数必须为正数: %s", pageSize);
    }
}

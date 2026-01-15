package cool.houge.mahu.domain;

import static java.util.Objects.requireNonNullElse;

import com.google.common.primitives.Ints;
import io.helidon.common.parameters.Parameters;
import org.jspecify.annotations.NonNull;

/// 分页请求的默认实现类
///
/// @author ZY (kzou227@qq.com)
class PageRequest implements Page {

    private final int page;
    private final int perPage;
    private final boolean includeTotal;
    private final Sort sort;

    PageRequest(int page, int perPage, Sort sort) {
        this(page, perPage, true, sort);
    }

    PageRequest(int page, int perPage, boolean includeTotal, Sort sort) {
        if (page <= 0) {
            throw new IllegalArgumentException("页码必须为正数: " + page);
        }
        if (perPage <= 0) {
            throw new IllegalArgumentException("页大小必须为正数: " + perPage);
        }

        this.page = page;
        this.perPage = perPage;
        this.includeTotal = includeTotal;
        this.sort = requireNonNullElse(sort, Sort.unsorted());
    }

    @Override
    public boolean isUnpaged() {
        return false;
    }

    @Override
    public boolean isIncludeTotal() {
        return includeTotal;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getPerPage() {
        return perPage;
    }

    @Override
    public long getOffset() {
        return (long) (page - 1) * perPage;
    }

    @Override
    public @NonNull Sort getSort() {
        return sort;
    }

    static Page of(Parameters params) {
        var sort = Sort.of(params);
        var page = params.first("page")
                .map(s -> requireNonNullElse(Ints.tryParse(s), DEFAULT_PAGE))
                .orElse(DEFAULT_PAGE);
        var perPage = params.first("per_page")
                .map(s -> requireNonNullElse(Ints.tryParse(s), DEFAULT_PER_PAGE))
                .orElse(DEFAULT_PER_PAGE);
        var includeDeleted = params.first("include_total").asBoolean().orElse(true);
        return new PageRequest(page, perPage, includeDeleted, sort);
    }
}

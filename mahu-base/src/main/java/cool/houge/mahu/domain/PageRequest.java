package cool.houge.mahu.domain;

import static java.util.Objects.requireNonNullElse;

import org.jspecify.annotations.NonNull;

/// 分页请求的默认实现类
///
/// @author ZY (kzou227@qq.com)
class PageRequest implements Page {

    private final int page;
    private final int perPage;
    private final boolean includeTotal;
    private final Sort sort;

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
}

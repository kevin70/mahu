package cool.houge.mahu.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/// FIQL 数据过滤
///
/// @author ZY (kzou227@qq.com)
@ToString
@Builder
@AllArgsConstructor
public final class FIQLFilter implements Pageable {

    private final Pageable page;
    /// FIQL 过滤查询语句
    @Getter
    private final String filter;
    /// 是否在已删除的数据中搜索
    @Getter
    private final boolean includeDeleted;
    /// 是否返回总记录数量
    @Getter
    private final boolean noTotalCount;

    @Override
    public int getPageNumber() {
        return page.getPageNumber();
    }

    @Override
    public int getPageSize() {
        return page.getPageSize();
    }

    @Override
    public long getOffset() {
        return page.getOffset();
    }

    @Override
    public Sort getSort() {
        return page.getSort();
    }
}

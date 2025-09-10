package cool.houge.mahu.domain;

import static java.util.Objects.requireNonNullElse;

/// 分页请求的默认实现类
///
/// @author ZY (kzou227@qq.com)
public class PageRequest implements Pageable {

    private final int pageNumber;
    private final int pageSize;
    private final Sort sort;

    /**
     * 私有构造器，通过静态工厂方法创建实例
     */
    private PageRequest(int pageNumber, int pageSize, Sort sort) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("页码不能为负数: " + pageNumber);
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("页大小必须为正数: " + pageSize);
        }

        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sort = requireNonNullElse(sort, Sort.unsorted());
    }

    /// 创建分页请求实例
    ///
    /// @param pageNumber 页码（从`0`开始）
    /// @param pageSize 页大小
    /// @return 分页请求实例
    public static PageRequest of(int pageNumber, int pageSize) {
        return new PageRequest(pageNumber, pageSize, Sort.unsorted());
    }

    /// 创建带排序的分页请求实例
    /// @param pageNumber 页码（从`0`开始）
    /// @param pageSize 页大小
    /// @param sort 排序参数
    /// @return 分页请求实例
    public static PageRequest of(int pageNumber, int pageSize, Sort sort) {
        return new PageRequest(pageNumber, pageSize, sort);
    }

    @Override
    public boolean isUnpaged() {
        return false;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public long getOffset() {
        return (long) pageNumber * pageSize;
    }

    @Override
    public Sort getSort() {
        return sort;
    }
}

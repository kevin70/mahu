package io.ebean.cool.houge;

import io.ebean.PagedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/// 没有总记录数的分页列表
///
/// @author ZY (kzou227@qq.com)
public class NoTotalPagedList<T> implements PagedList<T> {

    private final PagedList<T> delegate;

    public NoTotalPagedList(PagedList<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void loadCount() {}

    @Override
    public Future<Integer> getFutureCount() {
        return CompletableFuture.completedFuture(0);
    }

    @Override
    public List<T> getList() {
        return delegate.getList();
    }

    @Override
    public int getTotalCount() {
        return 0;
    }

    @Override
    public int getTotalPageCount() {
        return 0;
    }

    @Override
    public int getPageSize() {
        return delegate.getPageSize();
    }

    @Override
    public int getPageIndex() {
        return delegate.getPageIndex();
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public boolean hasPrev() {
        return false;
    }

    @Override
    public String getDisplayXtoYofZ(String to, String of) {
        return delegate.getDisplayXtoYofZ(to, of);
    }
}

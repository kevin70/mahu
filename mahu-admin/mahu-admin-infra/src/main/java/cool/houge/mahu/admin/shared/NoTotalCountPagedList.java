package cool.houge.mahu.admin.shared;

import io.ebean.PagedList;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/// 没有总记录数的分页数据列表
///
/// @author ZY (kzou227@qq.com)
class NoTotalCountPagedList<T> implements PagedList<T> {

    private final PagedList<T> plist;

    NoTotalCountPagedList(PagedList<T> plist) {
        this.plist = plist;
    }

    @Override
    public void loadCount() {
        // do nothing
    }

    @Override
    public Future<Integer> getFutureCount() {
        return CompletableFuture.completedFuture(-1);
    }

    @Override
    public List<T> getList() {
        return plist.getList();
    }

    @Override
    public int getTotalCount() {
        return -1;
    }

    @Override
    public int getTotalPageCount() {
        return -1;
    }

    @Override
    public int getPageSize() {
        return -1;
    }

    @Override
    public int getPageIndex() {
        return -1;
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
        return plist.getDisplayXtoYofZ(to, of);
    }
}

package cool.houge.mahu.admin.shared;

import cool.houge.mahu.common.DataFilter;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/// 公共的共享服务
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class SharedToolService {

    /// 包装分页数据
    ///
    /// @param plist      被包装的分页数据列表
    /// @param dataFilter 数据过滤
    public <T> PagedList<T> wrap(PagedList<T> plist, DataFilter dataFilter) {
        if (dataFilter.isNoTotalCount()) {
            return new NoTotalCountPagedList<>(plist);
        }
        return plist;
    }

    protected static class NoTotalCountPagedList<T> implements PagedList<T> {

        private final PagedList<T> plist;

        private NoTotalCountPagedList(PagedList<T> plist) {
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

}

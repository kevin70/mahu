package cool.houge.mahu.admin.shared;

import cool.houge.mahu.common.DataFilter;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

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
}

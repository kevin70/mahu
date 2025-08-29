package cool.houge.mahu.domain;

import io.ebean.PagedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/// 数据过滤
///
/// @author ZY (kzou227@qq.com)
public interface DataFilter {

    /// RSQL 数据过滤
    Optional<String> query();

    /// 返回是否包含软删除的数据
    boolean isIncludeDeleted();

    /// 是否不返回总记录数
    boolean isNoTotal();

    /// 输出查询结果
    <I, T> Result<T> toResult(PagedList<I> plist, Function<I, T> fn);

    interface Result<T> {

        /// RSQL 过滤对象
        DataFilter getFilter();

        /// 总记录数
        int getTotalCount();

        /// 记录列表
        List<T> getItems();
    }
}

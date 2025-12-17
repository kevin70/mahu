package cool.houge.mahu.domain;

import io.ebean.PagedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.jspecify.annotations.NonNull;

/// 数据过滤
///
/// @author ZY (kzou227@qq.com)
public interface DataFilter {

    /// RSQL 数据过滤
    Optional<String> filter();

    /// 分页参数
    Pageable page();

    /// 排序参数
    Sort sort();

    /// 返回是否包含软删除的数据
    boolean includeDeleted();

    /// 是否不返回总记录数
    boolean noTotal();

    /// 输出查询结果
    ///
    /// @param plist   分页列表
    /// @param mapping 分页列表对象映射
    <I, T> @NonNull Result<T> toResult(@NonNull PagedList<I> plist, @NonNull Function<I, T> mapping);

    interface Result<T> {

        /// RSQL 过滤对象
        DataFilter getFilter();

        /// 总记录数
        int getTotalItems();

        /// 记录列表
        List<T> getItems();
    }
}

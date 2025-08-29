package cool.houge.mahu.domain;

import com.google.common.collect.Lists;
import io.ebean.PagedList;
import java.util.List;
import java.util.function.Function;

/// 分页响应
///
/// @author ZY (kzou227@qq.com)
public interface PageResponse<T> {

    /// RSQL 过滤对象
    DataFilter getFilter();

    /// 总记录数
    Integer getTotalCount();

    /// 记录列表
    List<T> getItems();

    /// 分页响应对象
    static <I, O> PageResponse<O> of(DataFilter filter, PagedList<I> pagedList, Function<I, O> fn) {
        return new PageResponse<>() {
            @Override
            public DataFilter getFilter() {
                return filter;
            }

            @Override
            public Integer getTotalCount() {
                if (filter.isNoTotal()) {
                    return null;
                }
                return pagedList.getTotalCount();
            }

            @Override
            public List<O> getItems() {
                return Lists.transform(pagedList.getList(), fn::apply);
            }
        };
    }
}

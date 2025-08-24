package cool.houge.mahu.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.ebean.PagedList;
import java.util.List;
import java.util.function.Function;
import lombok.Data;

/// 分页响应
///
/// @author ZY (kzou227@qq.com)
@Data
public class PageResponse<T> {

    /// 总记录数
    @JsonProperty("total_count")
    private Integer totalCount;
    /// 记录列表
    private List<T> items;

    /// 转换为分页响应对象
    public static <I, O> PageResponse<O> of(PagedList<I> pagedList, Function<I, O> fn) {
        return new PageResponse<O>()
                .setTotalCount(pagedList.getTotalCount())
                .setItems(pagedList.getList().stream().map(fn).toList());
    }
}

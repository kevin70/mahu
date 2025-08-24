package cool.houge.mahu.util;

import java.util.List;
import org.jspecify.annotations.NonNull;

/// 数据过滤
///
/// @author ZY (kzou227@qq.com)
public interface DataFilter extends Paging {

    /// 返回排序的对象
    @NonNull List<String> sorts();

    /// RSQL 数据过滤
    String filter();

    /// 返回是否包含软删除的数据
    default boolean isIncludeDeleted() {
        return false;
    }

    /// 是否不返回总记录数
    default boolean isNoTotalCount() {
        return false;
    }
}

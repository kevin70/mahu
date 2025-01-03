package cool.houge.mahu.common;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/// 数据过滤
///
/// @author ZY (kzou227@qq.com)
public interface DataFilter {

    /// 查询默认返回的最大行数
    int DEFAULT_LIMIT = 20;

    /// 此查询要返回的第一行
    int offset();

    /// 查询中要返回的最大行数
    int limit();

    /// 返回排序的对象
    @NonNull
    List<String> sorts();

    /// RSQL 数据过滤
    String filter();

    /// 页面令牌
    String pageToken();

    /// 返回是否包含软删除的数据
    default boolean isIncludeDeleted() {
        return false;
    }

    /// 是否不返回总记录数
    default boolean isNoTotalCount() {
        return false;
    }
}

package cool.houge.mahu.util;

/// 分页参数
///
/// @author ZY (kzou227@qq.com)
public interface Paging {

    /// 查询默认返回的最大行数
    int DEFAULT_LIMIT = 20;

    /// 是否需要分页
    default boolean hasPage() {
        return false;
    }

    /// 此查询要返回的第一行
    int offset();

    /// 查询中要返回的最大行数
    int limit();
}

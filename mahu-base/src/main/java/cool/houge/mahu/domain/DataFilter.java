package cool.houge.mahu.domain;

import java.util.Optional;

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
}

package cool.houge.mahu.model.query;

import cool.houge.mahu.domain.DateRange;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;

/// 管理员日志查询
///
/// 仅用于构造后台日志分页查询条件。
///
/// @author ZY (kzou227@qq.com)
@Value
@Builder
public class AdminLogQuery {

    /// 管理员 ID（可选）
    Integer adminId;

    /// 创建时间区间（可选，默认不限制）
    @Default
    DateRange createdAtRange = DateRange.EMPTY;
}

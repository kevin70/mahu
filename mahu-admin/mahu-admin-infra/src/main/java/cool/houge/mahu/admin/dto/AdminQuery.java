package cool.houge.mahu.admin.dto;

import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;

/// 管理员查询
///
/// @author ZY (kzou227@qq.com)
@Value
@Builder
public class AdminQuery {

    /// 登录名
    String username;
    /// 状态
    @Default
    List<Integer> statusList = List.of();
}

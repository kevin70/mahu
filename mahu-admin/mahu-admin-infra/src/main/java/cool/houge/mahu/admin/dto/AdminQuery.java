package cool.houge.mahu.admin.dto;

import java.util.List;
import lombok.Data;

/// 管理员查询
///
/// @author ZY (kzou227@qq.com)
@Data
public class AdminQuery {

    /// 状态
    private List<Integer> statusList;
    /// 登录名
    private String username;
}

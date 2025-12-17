package cool.houge.mahu.admin.bean;

import java.util.List;
import lombok.Data;

/// 个人信息
///
/// @author ZY (kzou227@qq.com)
@Data
public class Profile {

    /// 管理员 ID.
    private Long adminId;
    /// 用户昵称.
    private String nickname;
    /// 用户头像.
    private String avatar;
    /// 用户权限代码
    private List<String> permissionCodes;
}

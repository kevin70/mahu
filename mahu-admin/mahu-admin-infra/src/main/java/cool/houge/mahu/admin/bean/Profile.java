package cool.houge.mahu.admin.bean;

import java.util.List;
import lombok.Data;

/// 个人信息
///
/// @author ZY (kzou227@qq.com)
@Data
public class Profile {

    /// 用户 ID.
    private Long uid;
    /// 用户昵称.
    private String nickname;
    /// 用户头像.
    private String avatar;
    /// 用户权限.
    private List<String> permits;

    // ========================================================= //

    /// 用户商店.
    private List<Shop> shops;

    @Data
    public static class Shop {
        /// 商店
        private Integer id;
        /// 商店名称
        private String name;
    }
}

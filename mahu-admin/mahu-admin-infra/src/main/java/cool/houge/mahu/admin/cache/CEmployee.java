package cool.houge.mahu.admin.cache;

import lombok.Data;

import java.util.List;

/// 缓存的职员对象
///
/// @author ZY (kzou227@qq.com)
@Data
public class CEmployee {

    /**
     * 员工ID.
     */
    private long id;
    /**
     * 用户昵称.
     */
    private String nickname;
    /**
     * 拥有角色代码.
     */
    private List<String> roleCodes;
}

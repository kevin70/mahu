package cool.houge.mahu.entity.sys;

import cool.houge.mahu.entity.Auditable;
import io.ebean.annotation.ChangeLog;
import io.ebean.annotation.SoftDelete;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/// 管理员表
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "admin", schema = "sys")
@ChangeLog(updatesThatInclude = {"nickname", "avatar", "status", "roles"})
public class Admin implements Auditable {

    /// 主键
    @Id
    @GeneratedValue
    private Integer id;
    /// 创建时间
    @WhenCreated
    private Instant createdAt;
    /// 更新时间
    @WhenModified
    private Instant updatedAt;
    /// 软删除
    @SoftDelete
    private Boolean deleted;
    /// 用户名
    private String username;
    /// 登录密码
    private String password;
    /// 昵称
    private String nickname;
    /// 头像地址
    private String avatar;
    /// 状态
    ///
    /// - [cool.houge.mahu.Status#ACTIVE]
    /// - [cool.houge.mahu.Status#DISABLED]
    private Integer status;
    /// 用户角色
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(schema = "sys", name = "admin_role")
    private List<Role> roles;

    /* ========================== 非持久化属性 ========================== */

    /// 原始密码（修改密码）
    @Transient
    private String originalPassword;

    /// 返回用户拥有的所有角色代码
    public Collection<String> allPermissions() {
        var codes = new LinkedHashSet<String>();
        for (Role role : this.getRoles()) {
            if (role.getPermissions() == null || role.getPermissions().isEmpty()) {
                continue;
            }
            codes.addAll(role.getPermissions());
        }
        return codes;
    }
}

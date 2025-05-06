package cool.houge.mahu.entity.system;

import cool.houge.mahu.entity.Auditable;
import io.ebean.annotation.SoftDelete;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/// 管理员表
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "admin", schema = "system")
public class Admin implements Auditable {

    /// 主键
    @Id
    @GeneratedValue
    private Long id;
    /// 创建时间
    @WhenCreated
    private Instant createdAt;
    /// 更新时间
    @WhenModified
    private Instant updatedAt;
    /// 软删除
    @SoftDelete
    private boolean deleted;
    /// 用户名
    private String username;
    /// 登录密码
    private String password;
    /// 昵称
    private String nickname;
    /// 头像地址
    private String avatar;
    /// 用户状态
    @Enumerated
    private Status status;
    /// 用户部门
    @ManyToOne
    private Department department;

    /// 用户角色
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "admin_role",
            schema = "system",
            joinColumns = @JoinColumn(name = "admin_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    /// 原始密码（修改密码）
    private transient String originalPassword;

    /// 返回用户拥有的所有角色代码
    public Collection<String> allRolePermits() {
        var roles = this.getRoles();
        var codes = new LinkedHashSet<String>();
        for (Role role : roles) {
            if (role.getPermits() == null || role.getPermits().isEmpty()) {
                continue;
            }
            codes.addAll(role.getPermits());
        }
        return codes;
    }

    public enum Status {
        /// 无
        NONE,
        /// 活跃的
        ACTIVE,
        /// 禁用的
        ///
        /// _是由管理员手动禁用_
        BLOCKED,
    }
}

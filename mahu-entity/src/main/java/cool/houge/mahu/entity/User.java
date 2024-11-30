package cool.houge.mahu.entity;

import io.ebean.annotation.SoftDelete;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/// 用户
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    /// 主键
    @Id
    @GeneratedValue
    private Long id;
    /// 创建时间
    @WhenCreated
    private Instant createTime;
    /// 更新时间
    @WhenModified
    private Instant updateTime;
    /// 软删除
    @SoftDelete
    private Boolean deleted;
    /// 头像地址
    private String avatar;
    /// 昵称
    private String nickname;
    /// 手机
    private String mobile;

    /// 手机状态
    public boolean getMobileStatus() {
        return mobile != null && !mobile.isEmpty();
    }
}

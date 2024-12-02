package cool.houge.mahu.entity;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/// 微信个人信息
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "wechat_profile")
public class WechatProfile {

    /// 主键
    @Id
    private Long id;
    /// 创建时间
    @WhenCreated
    private Instant createTime;
    /// 更新时间
    @WhenModified
    private Instant updateTime;
    /// 微信应用 ID
    private String appid;
    /// 微信 OpenID
    private String openid;
    /// 微信帐户唯一 ID
    private String unionid;

    /// 用户映射
    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private User user;
}

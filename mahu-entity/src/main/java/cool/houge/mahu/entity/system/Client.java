package cool.houge.mahu.entity.system;

import io.ebean.annotation.SoftDelete;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.OffsetDateTime;

/// 认证客户端
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "system", name = "client")
public class Client {

    /// 客户端 ID
    @Id
    private String clientId;
    /// 创建时间
    @WhenCreated
    private Instant createTime;
    /// 更新时间
    @WhenModified
    private Instant updateTime;
    /// 软删除
    @SoftDelete
    private Boolean deleted;
    /// 客户端密钥
    private String clientSecret;
    /// 备注
    private String remark;
    /// 标签
    private String label;
    /// 终端类型
    @Enumerated(EnumType.STRING)
    private TerminalType terminalType;
    /// 微信应用 ID
    private String wechatAppid;
    /// 微信应用密钥
    private String wechatAppsecret;
    /// 微信客户端访问令牌
    private String wechatAccessToken;
    /// 微信访问令牌过期时间
    private OffsetDateTime wechatAccessTokenExpireTime;
}

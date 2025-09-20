package cool.houge.mahu.entity;

import io.ebean.annotation.SoftDelete;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/// 认证客户端
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "auth_client")
public class AuthClient implements Auditable {

    /// 客户端 ID
    @Id
    private String clientId;
    /// 创建时间
    @WhenCreated
    private Instant createdAt;
    /// 更新时间
    @WhenModified
    private Instant updatedAt;
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
}

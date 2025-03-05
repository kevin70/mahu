package cool.houge.mahu.entity.system;

import io.ebean.annotation.WhenCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/// 访问令牌日志
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "system", name = "token_jour")
public class TokenJour {

    /// 主键
    @Id
    private String id;
    /// 创建时间
    @WhenCreated
    private LocalDateTime createdAt;
    /// 用户主体
    private String upn;
    /// 客户端ID
    private String clientId;
    /// 客户端IP
    private String clientAddr;
    /// 授权类型
    private String grantType;
}

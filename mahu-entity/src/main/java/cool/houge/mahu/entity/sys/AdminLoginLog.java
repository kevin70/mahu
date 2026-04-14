package cool.houge.mahu.entity.sys;

import io.ebean.annotation.WhenCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/// 管理员登录日志
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "sys", name = "admin_login_logs")
public class AdminLoginLog {

    public static final String REASON_ADMIN_NOT_FOUND = "ADMIN_NOT_FOUND";
    public static final String REASON_PASSWORD_MISMATCH = "PASSWORD_MISMATCH";
    public static final String REASON_ADMIN_DISABLED = "ADMIN_DISABLED";
    public static final String REASON_UNSUPPORTED_GRANT_TYPE = "UNSUPPORTED_GRANT_TYPE";
    public static final String REASON_CLIENT_NOT_FOUND = "CLIENT_NOT_FOUND";
    public static final String REASON_TOKEN_INVALID = "TOKEN_INVALID";
    public static final String REASON_TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String REASON_INTERNAL_ERROR = "INTERNAL_ERROR";

    /// 日志追踪 ID
    @Id
    private UUID id;
    /// 创建时间
    @WhenCreated
    private Instant createdAt;
    /// 命中的管理员 ID
    private Integer adminId;
    /// 授权类型
    private String grantType;
    /// 认证终端 ID
    private String clientId;
    /// 登录名快照
    private String username;
    /// 是否成功
    private Boolean success;
    /// 失败原因码
    private String reasonCode;
    /// 失败原因摘要
    private String reasonDetail;
    /// 访问 IP
    private String ipAddr;
    /// HTTP User-Agent
    private String userAgent;
}

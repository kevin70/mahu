package cool.houge.mahu.entity.sys;

import io.ebean.annotation.WhenCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/// 管理员认证日志
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "sys", name = "admin_auth_logs")
public class AdminAuthLog {

    /// 日志追踪 ID
    @Id
    protected UUID id;
    /// 创建时间
    @WhenCreated
    protected Instant createdAt;
    /// 操作管理员 ID
    ///
    /// 管理员认证记录，此时认证还未完成缺少上下文对象无法使用 `@WhoCreated` 获取当前认证用户信息。
    /// 此处需要在创建位置手动赋值。
    private Integer adminId;
    /// 授权类型
    private String grantType;
    /// 认证终端 ID
    private String clientId;
    /// 认证终端 IP
    private String ipAddr;
    /// HTTP User-Agent
    private String userAgent;
}

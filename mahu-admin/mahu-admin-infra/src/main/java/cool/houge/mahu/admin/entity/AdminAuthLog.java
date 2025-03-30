package cool.houge.mahu.admin.entity;

import cool.houge.mahu.entity.log.BaseBizLog;
import io.ebean.annotation.WhoCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/// 管理员认证日志
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "log", name = "admin_auth_log")
public class AdminAuthLog extends BaseBizLog {

    /// 操作管理员 ID
    @WhoCreated
    private Long adminId;
    /// 认证类型
    private String authType;
    /// 认证方法
    private String authMethod;
    /// 认证终端 ID
    private String clientId;
    /// 认证终端 IP
    private String ipAddr;
    /// HTTP User-Agent
    private String userAgent;
}

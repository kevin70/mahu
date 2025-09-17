package cool.houge.mahu.admin.entity;

import cool.houge.mahu.entity.log.BaseBizLog;
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
@Table(schema = "system", name = "admin_auth_log")
public class AdminAuthLog extends BaseBizLog {

    /// 操作管理员 ID
    ///
    /// 管理员认证记录，此时认证还未完成缺少上下文对象无法使用 `@WhoCreated` 获取当前认证用户信息。
    /// 此处需要在创建位置手动赋值。
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

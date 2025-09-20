package cool.houge.mahu.admin.entity;

import cool.houge.mahu.entity.log.BaseBizLog;
import io.ebean.annotation.DbJsonB;
import io.ebean.annotation.WhoCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/// 管理员操作审计日志
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "sys", name = "admin_audit_log")
public class AdminAuditLog extends BaseBizLog {

    /// 操作管理员 ID
    @WhoCreated
    private Long adminId;
    /// 操作 IP
    private String ipAddr;
    /// 事件类型
    private String changeType;
    /// 修改表
    private String tableName;
    /// 数据租户 ID
    private String dataTenantId;
    /// 数据 ID
    private String dataId;
    /// 新数据
    @DbJsonB
    private String data;
    /// 旧数据
    @DbJsonB
    private String oldData;
}

package cool.houge.mahu.entity.system;

import io.ebean.annotation.DbJsonB;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhoCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/// 修改审计日志
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "system", name = "audit_jour")
public class AuditJour {

    /// 主键
    @Id
    @GeneratedValue
    private Long id;
    /// 创建时间
    @WhenCreated
    private Instant createTime;
    /// 修改来源，应用名称
    private String source;
    /// 修改用户
    @WhoCreated
    private Long userId;
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
    /// 更新数据
    @DbJsonB
    private String data;
}

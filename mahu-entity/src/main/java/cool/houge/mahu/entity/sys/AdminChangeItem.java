package cool.houge.mahu.entity.sys;

import io.ebean.annotation.WhenCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/// 管理员操作审计日志项
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "sys", name = "admin_change_items")
public class AdminChangeItem {

    /// 日志 ID
    @Id
    private UUID id;
    /// 创建时间
    @WhenCreated
    private Instant createdAt;
    /// 变更记录
    @ManyToOne
    private AdminChangeLog changeLog;
    /// 表名
    private String tableName;
    /// 租户 ID
    private String tenantId;
    /// 数据 ID
    private String dataId;
    /// 变更类型
    private String changeType;
    /// 事件时间
    private Long eventTime;
    /// 新数据
    private String data;
    /// 旧数据
    private String oldData;
}

package cool.houge.mahu.entity.sys;

import io.ebean.annotation.DbJsonB;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/// 管理员通知主表
@Getter
@Setter
@Entity
@Table(schema = "sys", name = "admin_notifications")
public class AdminNotification {

    /// 发送范围：定向
    public static final int SCOPE_DIRECTED = 1;
    /// 发送范围：全局
    public static final int SCOPE_GLOBAL = 2;

    /// 主键
    @Id
    @GeneratedValue
    private Long id;

    /// 通知标题
    private String title;

    /// 通知内容
    private String content;

    /// 发送范围：1定向，2全局
    private Integer scope;

    /// 通知类型
    private Integer type;

    /// 状态：22生效，90过期
    /// @see cool.houge.mahu.config.Status#ACTIVE
    /// @see cool.houge.mahu.config.Status#EXPIRED
    private Integer status;

    /// 扩展载荷（JSON对象）
    @DbJsonB
    private Map<String, Object> payload;

    /// 过期时间，NULL表示不过期
    private Instant expireAt;

    /// 创建时间
    @WhenCreated
    private Instant createdAt;

    /// 更新时间
    @WhenModified
    private Instant updatedAt;

    /// 定向接收人
    @OneToMany(mappedBy = "notification", fetch = FetchType.LAZY)
    private List<AdminNotificationTarget> targets;

    /// 已读记录
    @OneToMany(mappedBy = "notification", fetch = FetchType.LAZY)
    private List<AdminNotificationRead> reads;

    /// 当前上下文是否已读（非持久化字段）
    @Transient
    private Boolean read;

    /// 当前上下文已读时间（非持久化字段）
    @Transient
    private Instant readAt;
}

package cool.houge.mahu.entity.sys;

import io.ebean.annotation.WhenCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/// 管理员通知定向接收人
@Getter
@Setter
@Entity
@Table(schema = "sys", name = "admin_notification_targets")
public class AdminNotificationTarget {

    /// 主键
    @Id
    @GeneratedValue
    private Long id;

    /// 通知
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(insertable = false, updatable = false)
    private AdminNotification notification;

    /// 管理员ID
    private Integer adminId;

    /// 创建时间
    @WhenCreated
    private Instant createdAt;
}

package cool.houge.mahu.entity.sys;

import io.ebean.annotation.DbJsonB;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/// 管理员通知主表
@Getter
@Setter
@Entity
@Table(schema = "sys", name = "admin_notifications")
public class AdminNotification {

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
}

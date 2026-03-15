package cool.houge.mahu.entity.sys;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/// 延时任务
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "delayed_tasks", schema = "sys")
public class DelayedTask {

    /// 主键
    @Id
    private UUID id;
    /// 创建时间
    @WhenCreated
    private Instant createdAt;
    /// 更新时间
    @WhenModified
    private Instant updatedAt;
    /// 功能 ID
    private Integer featureId;
    /// 消息主题
    private String topic;
    /// 状态
    ///
    /// - [cool.houge.mahu.Status#PENDING]
    /// - [cool.houge.mahu.Status#PROCESSING]
    /// - [cool.houge.mahu.Status#COMPLETED]
    /// - [cool.houge.mahu.Status#ARCHIVED]
    private Integer status;
    /// 消息延迟到的绝对时间（精确到毫秒）
    private Instant delayUntil;
    /// 已执行次数
    private Integer attempts;
    /// 最大重试次数（包括首次执行）
    private Integer maxAttempts;
    /// 锁定时间
    private Instant lockAt;
    /// 锁租约(秒)，worker 处理任务允许的最长时间
    private Integer leaseSeconds;
    /// 消息内容存储业务所需的所有数据
    private String payload;
    /// 幂等键
    private String idempotencyKey;
}

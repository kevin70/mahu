package cool.houge.mahu.entity.system;

import cool.houge.mahu.entity.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/// 定时任务
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "scheduled_task", schema = "system")
public class ScheduledTask implements Auditable {

    /// 任务 ID
    @Id
    @Column(name = "task_instance")
    private String id;
    /// 任务名称
    private String taskName;
    /// 任务数据
    private String taskData;
    /// 执行时间
    private Instant executionTime;
    /// 是否在执行中
    private boolean picked;
    /// 执行者
    private String pickedBy;
    /// 最后执行成功时间
    private Instant lastSuccess;
    /// 最后执行失败时间
    private Instant lastFailure;
    /// 连续执行失败的次数
    private int consecutiveFailures;
    /// 最后执行心跳时间
    private Instant lastHeartbeat;
    /// 数据版本（乐观锁）
    @Version
    private long version;
    /// 执行优先级
    private int priority;
}

package cool.houge.mahu.entity.system;

import cool.houge.mahu.entity.Auditable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/// 定时任务
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "scheduled_tasks", schema = "system")
public class ScheduledTask implements Auditable {

    /// 主键
    @EmbeddedId
    private TaskId taskId;
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

    /// 返回任务名称
    public String taskName() {
        return taskId.getTaskName();
    }

    /// 返回任务实例
    public String taskInstance() {
        return taskId.getTaskInstance();
    }

    /// 任务 ID
    @Data
    @Embeddable
    public static class TaskId {
        /// 任务名称
        private String taskName;
        /// 任务实例
        private String taskInstance;
    }
}

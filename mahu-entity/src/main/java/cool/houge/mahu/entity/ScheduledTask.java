package cool.houge.mahu.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.time.ZonedDateTime;

/// 定时任务
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "scheduled_tasks")
public class ScheduledTask {

    /// 主键
    @EmbeddedId
    private TaskId taskId;
    /// 任务数据
    private ByteBuffer taskData;
    /// 执行时间
    private ZonedDateTime executionTime;
    /// 是否在执行中
    private String picked;
    /// 执行者
    private String pickedBy;
    /// 最后执行成功时间
    private ZonedDateTime lastSuccess;
    /// 最后执行失败时间
    private ZonedDateTime lastFailure;
    /// 连续执行失败的次数
    private int consecutiveFailures;
    /// 最后执行心跳时间
    private ZonedDateTime lastHeartbeat;
    /// 数据版本（乐观锁）
    @Version
    private long version;
    /// 执行优先级
    private int priority;
    /// 任务描述
    private String description;

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

package cool.houge.mahu.task.entity;

import io.ebean.annotation.DbJsonB;
import io.ebean.annotation.WhenCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/// 任务执行日志
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "sys", name = "scheduled_task_exe_log")
public class ScheduledTaskExeLog {

    /// 日志追踪 ID
    @Id
    protected UUID id;
    /// 创建时间
    @WhenCreated
    protected Instant createdAt;
    /// 定时任务
    @ManyToOne
    @JoinColumn(name = "task_name", referencedColumnName = "task_name")
    private ScheduledTask scheduledTask;
    /// 任务执行者
    private String pickedBy;
    /// 任务开始时间
    private Instant startTime;
    /// 任务结束时间
    private Instant doneTime;
    /// 执行是否成功
    private Boolean success;
    /// 日志追踪 ID
    private String traceId;
    /// 异常日志
    @DbJsonB
    private List<String> failCause;
}

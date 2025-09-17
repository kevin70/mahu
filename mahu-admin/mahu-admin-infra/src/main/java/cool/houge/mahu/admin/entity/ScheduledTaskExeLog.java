package cool.houge.mahu.admin.entity;

import cool.houge.mahu.entity.log.BaseBizLog;
import io.ebean.annotation.DbJsonB;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/// 任务执行日志
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "system", name = "scheduled_task_exe_log")
public class ScheduledTaskExeLog extends BaseBizLog {

    /// 定时任务
    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "task_instance")
    private ScheduledTask scheduledTask;
    /// 任务执行者
    private String pickedBy;
    /// 任务开始时间
    private Instant startedAt;
    /// 任务结束时间
    private Instant finishedAt;
    /// 执行是否成功
    private boolean succeeded;
    /// 日志追踪 ID
    private String traceId;
    /// 异常日志
    @DbJsonB
    private List<String> cause;
}

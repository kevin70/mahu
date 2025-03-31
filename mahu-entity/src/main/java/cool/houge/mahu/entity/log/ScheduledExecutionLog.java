package cool.houge.mahu.entity.log;

import cool.houge.mahu.entity.ScheduledTask;
import io.ebean.annotation.DbJsonB;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(schema = "log", name = "scheduled_execution_log")
public class ScheduledExecutionLog extends BaseBizLog {

    /// 定时任务
    @ManyToOne
    @JoinColumn(name = "task_name", referencedColumnName = "task_name")
    @JoinColumn(name = "task_instance", referencedColumnName = "task_instance")
    private ScheduledTask scheduledTask;
    /// 任务执行者
    private String pickedBy;
    /// 任务开始时间
    private LocalDateTime startedAt;
    /// 任务结束时间
    private LocalDateTime finishedAt;
    /// 执行是否成功
    private boolean succeeded;
    /// 异常日志
    @DbJsonB
    private List<String> cause;
}

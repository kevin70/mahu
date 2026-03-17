package cool.houge.mahu.entity.sys;

import io.ebean.annotation.ChangeLog;
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
@Table(name = "scheduled_tasks", schema = "sys")
@ChangeLog(
        updatesThatInclude = {
            "taskInstance",
            "taskData",
            "executionTime",
            "picked",
            "pickedBy",
            "lastSuccess",
            "lastFailure",
            "consecutiveFailures",
            "lastHeartbeat",
            "version",
            "priority"
        })
public class ScheduledTask {

    /// 任务名称
    @Id
    private String taskName;
    /// 任务实例
    private String taskInstance;
    /// 任务数据
    private String taskData;
    /// 执行时间
    private Instant executionTime;
    /// 是否在执行中
    private Boolean picked;
    /// 执行者
    private String pickedBy;
    /// 最后执行成功时间
    private Instant lastSuccess;
    /// 最后执行失败时间
    private Instant lastFailure;
    /// 连续执行失败的次数
    private Integer consecutiveFailures;
    /// 最后执行心跳时间
    private Instant lastHeartbeat;
    /// 数据版本（乐观锁）
    @Version
    private Long version;
    /// 执行优先级
    private Integer priority;
}

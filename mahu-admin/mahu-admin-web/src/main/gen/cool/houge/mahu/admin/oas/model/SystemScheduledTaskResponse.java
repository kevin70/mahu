package cool.houge.mahu.admin.oas.model;

import java.time.LocalDateTime;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SystemScheduledTaskResponse {

    /**
     * 任务名称
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("task_name")
    private String taskName;
    /**
     * 任务实例
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("task_instance")
    private String taskInstance;
    /**
     * 任务数据
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("task_data")
    private String taskData;
    /**
     * 下一次任务执行时间
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("execution_time")
    private LocalDateTime executionTime;
    /**
     * 任务是否在执行中
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("picked")
    private Boolean picked;
    /**
     * 任务执行者
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("picked_by")
    private String pickedBy;
    /**
     * 上次执行成功的时间
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("last_success")
    private LocalDateTime lastSuccess;
    /**
     * 上次执行失败的时间
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("last_failure")
    private LocalDateTime lastFailure;
    /**
     * 连续执行失败的次数
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("consecutive_failures")
    private Integer consecutiveFailures;
    /**
     * 最后健康检查的时间
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("last_heartbeat")
    private LocalDateTime lastHeartbeat;
    /**
     * 任务版本号（乐观锁）
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("version")
    private Long version;
    /**
     * 任务优先级
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("priority")
    private Integer priority;
}


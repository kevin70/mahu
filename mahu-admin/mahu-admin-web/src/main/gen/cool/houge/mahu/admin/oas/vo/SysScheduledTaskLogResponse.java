package cool.houge.mahu.admin.oas.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysScheduledTaskLogResponse {

    /**
     * 创建时间
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private LocalDateTime createdAt;
    /**
     * 任务名称
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("task_name")
    private String taskName;
    /**
     * 任务执行者
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("picked_by")
    private String pickedBy;
    /**
     * 执行开始时间
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("start_time")
    private LocalDateTime startTime;
    /**
     * 完成时间
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("done_time")
    private LocalDateTime doneTime;
    /**
     * 是否执行成功
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("success")
    private Boolean success;
    /**
     * 日志追踪 ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("trace_id")
    private String traceId;
    /**
     * 失败原因
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("fail_cause")
    private List<String> failCause;
}

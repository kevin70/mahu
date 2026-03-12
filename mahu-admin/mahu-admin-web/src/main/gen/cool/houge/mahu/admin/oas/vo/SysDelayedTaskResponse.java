package cool.houge.mahu.admin.oas.vo;

import java.time.LocalDateTime;
import java.util.UUID;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysDelayedTaskResponse {

    /**
     * ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private UUID id;
    /**
     * 创建时间
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private LocalDateTime createdAt;
    /**
     * 修改时间
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    /**
     * 功能 ID（用于追踪统计）
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("feature_id")
    private Integer featureId;
    /**
     * 主题
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("topic")
    private String topic;
    /**
     * 数据状态  - `10` `DRAFT` 草稿 - `11` `PENDING` 进行中 - `20` `APPROVED` 已批准/已通过/已授权 - `22` `ACTIVE` 活跃 - `30` `PAID` 已支付 - `54` `DISABLED` 已禁用 
     * minimum: 10
     * maximum: 99
     */
      @NotNull
 @Min(10) @Max(99)
    @com.fasterxml.jackson.annotation.JsonProperty("status")
    private Integer status;
    /**
     * 下一次可执行时间（含重试）
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("delay_until")
    private LocalDateTime delayUntil;
    /**
     * 已尝试次数
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("attempts")
    private Integer attempts;
    /**
     * 最大重试次数（包括首次执行）
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("max_attempts")
    private Integer maxAttempts;
    /**
     * 任务锁定时间（开始执行时间）
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("lock_at")
    private LocalDateTime lockAt;
    /**
     * 锁租约(秒)，worker 处理任务允许的最长时间
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("lease_seconds")
    private Integer leaseSeconds;
    /**
     * 消息内容（JSON 字符串）
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("payload")
    private String payload;
    /**
     * 幂等键
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("idempotency_key")
    private String idempotencyKey;
}

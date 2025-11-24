package cool.houge.mahu.admin.oas.vo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysDelayMessageResponse {

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
     * 主题
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("topic")
    private String topic;
    /**
     * 数据状态  - `10` `DRAFT` 草稿 - `11` `PENDING` 进行中 - `20` `APPROVED` 已批准/已通过/已授权 - `22` `ACTIVE` 活跃 - `30` `PAID` 已支付 - `76` `DISABLED` 已禁用 
     * minimum: 10
     * maximum: 99
     */
     @Min(10) @Max(99)
    @com.fasterxml.jackson.annotation.JsonProperty("status")
    private Integer status;
    /**
     * 消息延迟到的绝对时间（精确到毫秒）
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("delay_until")
    private LocalDateTime delayUntil;
    /**
     * 初始重试间隔（1000 毫秒，即 1 秒）
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("delay")
    private Integer delay;
    /**
     * 重试间隔的倍数（指数退避策略，间隔依次为 1s、2s、4s、8s...）
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("multiplier")
    private Integer multiplier;
    /**
     * 最大重试间隔（10000 毫秒，即 10 秒）
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("max_delay")
    private Integer maxDelay;
    /**
     * 最大重试次数（包括首次执行）
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("max_attempts")
    private Integer maxAttempts;
    /**
     * 重试次数
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("retry_count")
    private Integer retryCount;
    /**
     * 消息内容
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("body")
    private Map<String, Object> body;
    /**
     * 功能 ID（用于追踪统计）
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("feature_id")
    private Integer featureId;
}

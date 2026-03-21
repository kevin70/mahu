package cool.houge.mahu.admin.oas.vo;

import java.time.OffsetDateTime;
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
    private OffsetDateTime createdAt;
    /**
     * 修改时间
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("updated_at")
    private OffsetDateTime updatedAt;
    /**
     * 功能 ID（用于追踪统计）
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("feature_id")
    private Integer featureId;
    /**
     * 业务侧实体主键/关联 ID（必填）
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("reference_id")
    private String referenceId;
    /**
     * 主题
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("topic")
    private String topic;
    /**
     * 数据状态  - `10` `DRAFT` 草稿 - `11` `PENDING` 待处理 - `12` `SUSPENDED` 暂停 - `16` `PROCESSING` 进行中 - `20` `APPROVED` 已批准 - `22` `ACTIVE` 活跃 - `30` `PARTIAL_PAID` 部分支付 - `31` `PAID` 已支付 - `32` `PARTIAL_REFUNDED` 部分退款 - `33` `REFUNDED` 已退款 - `40` `PACKAGED` 已打包 - `41` `SHIPPED` 已发货 - `42` `IN_TRANSIT` 运输中 - `43` `DELIVERED` 已送达 - `72` `LOCKED` 锁定 - `74` `DISABLED` 已禁用 - `86` `REJECTED` 被拒绝 - `87` `VOIDED` 作废 - `88` `COMPLETED` 已完成 - `89` `FAILED` 失败 - `90` `EXPIRED` 已过期 - `92` `INACTIVE` 非活跃 - `93` `CANCELLED` 已取消 - `95` `DELETED` 已删除 - `97` `ARCHIVED` 归档 - `99` `TERMINATED` 已终止 
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
    private OffsetDateTime delayUntil;
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
    private OffsetDateTime lockAt;
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

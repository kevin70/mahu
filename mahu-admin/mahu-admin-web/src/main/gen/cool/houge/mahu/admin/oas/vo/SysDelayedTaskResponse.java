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
     * 数据状态  - `100` `DRAFT` 草稿 - `110` `CREATED` 已创建 - `120` `SUBMITTED` 已提交 - `130` `PENDING` 待处理 - `170` `PENDING_CONFIRMATION` 待确认 - `180` `PENDING_REVIEW` 待审核 - `190` `PENDING_PAYMENT` 待支付 - `200` `ACTIVE` 活跃 - `210` `APPROVED` 已批准 - `220` `PROCESSING` 处理中 - `230` `CONFIRMED` 已确认 - `240` `ACCEPTED` 已接受 - `250` `ASSIGNED` 已分配 - `260` `AVAILABLE` 可用 - `270` `PUBLISHED` 已发布 - `280` `RUNNING` 运行中 - `290` `RETRYING` 重试中 - `300` `PAID` 已支付 - `310` `PARTIALLY_PAID` 部分支付 - `320` `REFUNDING` 退款中 - `330` `REFUNDED` 已退款 - `340` `PARTIALLY_REFUNDED` 部分退款 - `400` `PACKAGED` 已打包 - `410` `SHIPPED` 已发货 - `420` `IN_TRANSIT` 运输中 - `430` `DELIVERED` 已送达 - `700` `LOCKED` 锁定 - `710` `SUSPENDED` 暂停 - `720` `DISABLED` 已禁用 - `730` `FROZEN` 已冻结 - `740` `BLOCKED` 已阻断 - `750` `INACTIVE` 非活跃 - `800` `COMPLETED` 已完成 - `810` `ARCHIVED` 归档 - `820` `CLOSED` 已关闭 - `830` `SETTLED` 已结算 - `840` `FULFILLED` 已履约 - `900` `FAILED` 失败 - `910` `REJECTED` 被拒绝 - `920` `EXPIRED` 已过期 - `930` `CANCELLED` 已取消 - `940` `VOIDED` 作废 - `950` `DELETED` 已删除 - `960` `TERMINATED` 已终止 - `970` `ROLLED_BACK` 已回滚 - `980` `TIMED_OUT` 已超时 - `990` `ABORTED` 已中止 
     * minimum: 100
     * maximum: 990
     */
      @NotNull
 @Min(100) @Max(990)
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

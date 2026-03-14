package cool.houge.mahu.admin.oas.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysFeatureUpdateRequest {

    /**
     * 名称
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    private String name;
    /**
     * 描述
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("description")
    private String description;
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
     * 生效开始时间（精确到秒）
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("effective_from")
    private LocalDateTime effectiveFrom;
    /**
     * 生效结束时间（精确到秒）
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("effective_to")
    private LocalDateTime effectiveTo;
    /**
     * 每天的开始时间（精确到秒）
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("start_time")
    private String startTime;
    /**
     * 每天的结束时间（精确到秒，结束时间小于开始时间代表跨天）
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("end_time")
    private String endTime;
    /**
     * 启用的星期
     */
     @Size(max=7)
    @com.fasterxml.jackson.annotation.JsonProperty("weekdays")
    private List<Integer> weekdays;
    /**
     * 可用的用户 IDs
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("allow_users")
    private List<Long> allowUsers;
    /**
     * 禁用的用户 IDs
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("deny_users")
    private List<Long> denyUsers;
    /**
     * 扩展属性
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("extra_properties")
    private Map<String, Object> extraProperties;
    /**
     * JSON Schema
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("extra_schema")
    private Map<String, Object> extraSchema;
}

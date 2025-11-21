package cool.houge.mahu.admin.oas.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysFeatureResponse {

    /**
     * 功能 ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private Integer id;
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
     * 模块
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("module")
    private String module;
    /**
     * 功能代码
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("code")
    private String code;
    /**
     * 功能名称
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    private String name;
    /**
     * 描述
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("description")
    private String description;
    /**
     * 数据状态  - `10` `DRAFT` 草稿 - `11` `PENDING` 进行中 - `20` `APPROVED` 已批准/已通过/已授权 - `22` `ACTIVE` 活跃 - `30` `PAID` 已支付 - `76` `DISABLED` 已禁用 
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
    
    @com.fasterxml.jackson.annotation.JsonProperty("weekdays")
    private List<Integer> weekdays;
    /**
     * 扩展属性
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("metadata")
    private String metadata;
    /**
     * `metadata` 元数据的 JSON Schema 
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("metadata_schema")
    private String metadataSchema;
}

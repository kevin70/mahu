package cool.houge.mahu.admin.oas.vo;

import java.time.OffsetDateTime;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysFeatureFlagUpdateRequest {

    /**
     * 全局唯一标识，preset 为 false 时允许修改；命名规范：{module}.{feature}，如 payment.wechat
     */
     @Size(min=1,max=128)
    @com.fasterxml.jackson.annotation.JsonProperty("code")
    private String code;
    /**
     * 名称
     */
      @NotNull
 @Size(min=1,max=128)
    @com.fasterxml.jackson.annotation.JsonProperty("name")
    private String name;
    /**
     * 描述
     */
     @Size(max=4096)
    @com.fasterxml.jackson.annotation.JsonProperty("description")
    private String description;
    /**
     * 开关状态：true=开启，false=关闭，修改后直接生效
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("enabled")
    private Boolean enabled;
    /**
     * 定时开启时间，NULL=不启用定时；定时任务到期后置 enabled=true 并清空此字段
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("enable_at")
    private OffsetDateTime enableAt;
    /**
     * 定时关闭时间，NULL=不启用定时；定时任务到期后置 enabled=false 并清空此字段
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("disable_at")
    private OffsetDateTime disableAt;
    /**
     * 排序值
     * minimum: 0
     * maximum: 65535
     */
     @Min(0) @Max(65535)
    @com.fasterxml.jackson.annotation.JsonProperty("ordering")
    private Integer ordering;
}

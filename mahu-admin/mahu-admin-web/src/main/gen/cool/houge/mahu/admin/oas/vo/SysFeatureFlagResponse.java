package cool.houge.mahu.admin.oas.vo;

import java.time.OffsetDateTime;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysFeatureFlagResponse {

    /**
     * 功能开关 ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private Integer id;
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
     * 全局唯一标识，程序通过此字段读取开关；命名规范：{module}.{feature}，如 payment.wechat
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("code")
    private String code;
    /**
     * 可读名称，如\"微信支付\"
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    private String name;
    /**
     * 开关用途说明，建议描述：功能用途 + 关闭后的影响
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("description")
    private String description;
    /**
     * 开关状态：true=开启，false=关闭，修改后直接生效
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("enabled")
    private Boolean enabled;
    /**
     * 系统预置标志：true=内置开关，禁止删除
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("preset")
    private Boolean preset;
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
      @NotNull
 @Min(0) @Max(65535)
    @com.fasterxml.jackson.annotation.JsonProperty("ordering")
    private Integer ordering;
}

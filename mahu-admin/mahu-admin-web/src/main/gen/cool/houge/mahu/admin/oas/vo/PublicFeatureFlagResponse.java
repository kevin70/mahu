package cool.houge.mahu.admin.oas.vo;

import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class PublicFeatureFlagResponse {

    /**
     * 功能开关代码，程序通过此字段读取开关
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("code")
    private String code;
    /**
     * 可读名称
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("name")
    private String name;
    /**
     * 开关用途说明
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("description")
    private String description;
    /**
     * 开关状态
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("active")
    private Boolean active;
}

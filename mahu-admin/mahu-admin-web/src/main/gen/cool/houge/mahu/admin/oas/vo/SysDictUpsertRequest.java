package cool.houge.mahu.admin.oas.vo;

import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysDictUpsertRequest {

    /**
     * 字典代码
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("dc")
    private Integer dc;
    /**
     * 字典数据标签
     */
      @NotNull
 @Size(min=1,max=255)
    @com.fasterxml.jackson.annotation.JsonProperty("label")
    private String label;
    /**
     * 字典类型描述
     */
     @Size(max=4096)
    @com.fasterxml.jackson.annotation.JsonProperty("value")
    private String value;
    /**
     * 排序值
     * minimum: 0
     * maximum: 65535
     */
      @NotNull
 @Min(0) @Max(65535)
    @com.fasterxml.jackson.annotation.JsonProperty("ordering")
    private Integer ordering;
    /**
     * 展示用颜色（如 CSS 色值）
     */
     @Size(max=64)
    @com.fasterxml.jackson.annotation.JsonProperty("color")
    private String color;
    /**
     * 是否启用
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("enabled")
    private Boolean enabled;
    /**
     * 是否预置
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("preset")
    private Boolean preset;
}

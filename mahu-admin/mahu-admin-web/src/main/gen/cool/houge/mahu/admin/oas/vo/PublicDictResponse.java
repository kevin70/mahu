package cool.houge.mahu.admin.oas.vo;

import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class PublicDictResponse {

    /**
     * 字典数据代码，唯一
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("dc")
    private Integer dc;
    /**
     * 字典类型描述
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("value")
    private String value;
    /**
     * 字典数据标签
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("label")
    private String label;
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

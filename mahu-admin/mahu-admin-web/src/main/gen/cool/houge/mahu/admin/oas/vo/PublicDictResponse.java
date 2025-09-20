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
     * 字典数据名称
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    private String name;
    /**
     * 字典类型描述
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("value")
    private String value;
    /**
     * 是否禁用
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("disabled")
    private Boolean disabled;
}


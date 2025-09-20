package cool.houge.mahu.admin.oas.vo;

import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class PublicDictResponse {

    /**
     * 字典数据代码，唯一
     */
      @NotNull
 @Size(min=1,max=50)
    @com.fasterxml.jackson.annotation.JsonProperty("code")
    private String code;
    /**
     * 字典数据名称
     */
      @NotNull
 @Size(min=1,max=100)
    @com.fasterxml.jackson.annotation.JsonProperty("name")
    private String name;
    /**
     * 字典类型描述
     */
     @Size(max=4096)
    @com.fasterxml.jackson.annotation.JsonProperty("value")
    private String value;
    /**
     * 是否禁用
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("disabled")
    private Boolean disabled;
}


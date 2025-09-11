package cool.houge.mahu.admin.oas.model;

import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class UpsertSystemDictRequest {

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
     * 排序值
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("ordering")
    private Integer ordering;
    /**
     * 状态（F:禁用, T:启用）
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("status")
    private Boolean status;
}


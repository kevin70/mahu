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
     * maximum: 999999
     */
      @NotNull
 @Min(0) @Max(999999)
    @com.fasterxml.jackson.annotation.JsonProperty("ordering")
    private Integer ordering;
    /**
     * 状态（F:禁用, T:启用）
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("disabled")
    private Boolean disabled;
}

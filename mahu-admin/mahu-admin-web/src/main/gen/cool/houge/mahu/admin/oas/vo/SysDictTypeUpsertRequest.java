package cool.houge.mahu.admin.oas.vo;

import cool.houge.mahu.admin.oas.vo.SysDictUpsertRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysDictTypeUpsertRequest {

    /**
     * 字典类型代码，唯一
     */
      @NotNull
 @Size(min=1,max=50)
    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private String id;
    /**
     * 字典类型名称
     */
      @NotNull
 @Size(min=1,max=255)
    @com.fasterxml.jackson.annotation.JsonProperty("name")
    private String name;
    /**
     * 字典类型描述
     */
     @Size(max=4096)
    @com.fasterxml.jackson.annotation.JsonProperty("description")
    private String description;
    /**
     * 是否禁用
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("disabled")
    private Boolean disabled;
    /**
     * 可见性 - 0: 私有的，仅限内部使用 - 1: 公共的 - 2: 受限的 
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("visibility")
    private Integer visibility;
    /**
     * 值正则表达式规则
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("value_regex")
    private String valueRegex;
    /**
     * 字典数据
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("data")
    private List<@Valid SysDictUpsertRequest> data;
}

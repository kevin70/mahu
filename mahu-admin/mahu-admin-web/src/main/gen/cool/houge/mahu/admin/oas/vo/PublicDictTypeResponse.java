package cool.houge.mahu.admin.oas.vo;

import cool.houge.mahu.admin.oas.vo.PublicDictResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class PublicDictTypeResponse {

    /**
     * 字典类型代码，唯一
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private String id;
    /**
     * 字典类型名称
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    private String name;
    /**
     * 字典类型描述
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("description")
    private String description;
    /**
     * 是否禁用
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("disabled")
    private Boolean disabled;
    /**
     * 字典数据
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("data")
    private List<@Valid PublicDictResponse> data;
}

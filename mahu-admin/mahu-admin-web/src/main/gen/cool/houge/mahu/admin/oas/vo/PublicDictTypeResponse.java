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
 @Size(min=1,max=50)
    @com.fasterxml.jackson.annotation.JsonProperty("type_code")
    private String typeCode;
    /**
     * 字典类型名称
     */
      @NotNull
 @Size(min=1,max=50)
    @com.fasterxml.jackson.annotation.JsonProperty("name")
    private String name;
    /**
     * 字典类型描述
     */
     @Size(max=255)
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


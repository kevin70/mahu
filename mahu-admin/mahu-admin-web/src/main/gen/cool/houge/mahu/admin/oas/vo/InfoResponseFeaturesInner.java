package cool.houge.mahu.admin.oas.vo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class InfoResponseFeaturesInner {

    /**
     * 名称唯一，命名规则为`模块.名称` 
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    private String name;
    /**
     * 功能描述
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("description")
    private String description;
    /**
     * 功能是否启用
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("enabled")
    private Boolean enabled;
}

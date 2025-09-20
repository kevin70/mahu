package cool.houge.mahu.admin.oas.vo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.LocalDateTime;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysDictTypeResponseDataInner {

    /**
     * 创建时间
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private LocalDateTime createdAt;
    /**
     * 修改时间
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    /**
     * 字典数据代码，唯一
     */
      @NotNull
 @Size(min=1,max=50)
    @com.fasterxml.jackson.annotation.JsonProperty("data_code")
    private String dataCode;
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
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("ordering")
    private Integer ordering;
    /**
     * 是否禁用
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("disabled")
    private Boolean disabled;
}


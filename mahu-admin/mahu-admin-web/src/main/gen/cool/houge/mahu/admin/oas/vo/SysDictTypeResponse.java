package cool.houge.mahu.admin.oas.vo;

import cool.houge.mahu.admin.oas.vo.SysDictTypeResponseDataInner;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysDictTypeResponse {

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
     * 是否启用
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("enabled")
    private Boolean enabled;
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
    private List<@Valid SysDictTypeResponseDataInner> data;
}

package cool.houge.mahu.admin.oas.model;

import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SystemPermissionResponse {

    /**
     * 权限代码
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("code")
    private String code;
    /**
     * 标签
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("label")
    private String label;
    /**
     * 是否可读取
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("can_read")
    private Boolean canRead;
    /**
     * 是否可写入
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("can_write")
    private Boolean canWrite;
    /**
     * 是否可删除
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("can_delete")
    private Boolean canDelete;
}


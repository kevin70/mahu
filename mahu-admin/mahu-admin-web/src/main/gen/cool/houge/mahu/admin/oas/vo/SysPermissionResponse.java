package cool.houge.mahu.admin.oas.vo;

import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysPermissionResponse {

    /**
     * 权限代码
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("code")
    private String code;
    /**
     * 模块
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("module")
    private String module;
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

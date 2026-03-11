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
     * 读取权限代码
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("read_code")
    private String readCode;
    /**
     * 写入权限代码
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("write_code")
    private String writeCode;
    /**
     * 删除权限代码
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("delete_code")
    private String deleteCode;
}

package cool.houge.mahu.admin.oas.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysRoleUpsertRequest {

    /**
     * 角色 ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private Integer id;
    /**
     * 名称
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    private String name;
    /**
     * 排序值
     * minimum: 0
     * maximum: 65535
     */
      @NotNull
 @Min(0) @Max(65535)
    @com.fasterxml.jackson.annotation.JsonProperty("ordering")
    private Integer ordering;
    /**
     * 字典备注
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("remark")
    private String remark;
    /**
     * 拥有的权限代码
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("permissions")
    private List<String> permissions;
}

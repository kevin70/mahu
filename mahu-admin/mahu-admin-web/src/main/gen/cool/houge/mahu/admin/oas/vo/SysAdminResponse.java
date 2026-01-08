package cool.houge.mahu.admin.oas.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysAdminResponse {

    /**
     * ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private Integer id;
    /**
     * 创建时间
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private LocalDateTime createdAt;
    /**
     * 修改时间
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    /**
     * 是否已经删除的数据
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("deleted")
    private Boolean deleted;
    /**
     * 用户名
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("username")
    private String username;
    /**
     * 昵称
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("nickname")
    private String nickname;
    /**
     * 头像地址
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("avatar")
    private String avatar;
    /**
     * 状态 - `11` 活跃 - `54` 已禁用 
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("status")
    private Integer status;
    /**
     * 角色 IDs
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("role_ids")
    private List<Integer> roleIds;
}

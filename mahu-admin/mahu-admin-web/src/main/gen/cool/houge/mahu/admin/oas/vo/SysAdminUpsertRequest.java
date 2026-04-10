package cool.houge.mahu.admin.oas.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysAdminUpsertRequest {

    /**
     * 用户名
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("username")
    private String username;
    /**
     * 登录密码
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("password")
    private String password;
    /**
     * 昵称
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("nickname")
    private String nickname;
    /**
     * 头像地址
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("avatar")
    private String avatar;
    /**
     * 状态 - `200` 活跃 - `720` 已禁用 
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("status")
    private Integer status;
    /**
     * 角色 IDs
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("role_ids")
    private List<Integer> roleIds;
}

package cool.houge.mahu.admin.oas.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class MeProfileResponse {

    /**
     * 用户ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("admin_id")
    private Long adminId;
    /**
     * 用户名
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("username")
    private String username;
    /**
     * 昵称
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("nickname")
    private String nickname;
    /**
     * 头像 URL
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("avatar")
    private String avatar;
    /**
     * 用户拥有的角色代码
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("permission_codes")
    private List<String> permissionCodes;
}

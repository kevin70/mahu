package cool.houge.mahu.admin.oas.vo;

import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class MePasswordUpdateRequest {

    /**
     * 原始密码
     */
      @NotNull
 @Size(max=64)
    @com.fasterxml.jackson.annotation.JsonProperty("original_password")
    private String originalPassword;
    /**
     * 新密码
     */
      @NotNull
 @Size(max=64)
    @com.fasterxml.jackson.annotation.JsonProperty("password")
    private String password;
}

package cool.houge.mahu.admin.oas.vo;

import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class TokenPasswordForm {

    /**
     * 授权类型
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("grant_type")
    private String grantType;
    /**
     * 请求客户端 ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("client_id")
    private String clientId;
    /**
     * 登录用户名
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("username")
    private String username;
    /**
     * 登录密码
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("password")
    private String password;
}

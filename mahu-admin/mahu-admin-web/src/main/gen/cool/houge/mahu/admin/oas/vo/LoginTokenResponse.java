package cool.houge.mahu.admin.oas.vo;

import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class LoginTokenResponse {

    /**
     * 令牌类型
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("token_type")
    private String tokenType;
    /**
     * 访问令牌
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("access_token")
    private String accessToken;
    /**
     * 过期时间（秒）
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("expires_in")
    private Integer expiresIn;
    /**
     * 刷新令牌
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("refresh_token")
    private String refreshToken;
}

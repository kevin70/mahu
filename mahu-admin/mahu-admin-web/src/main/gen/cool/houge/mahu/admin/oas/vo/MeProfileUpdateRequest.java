package cool.houge.mahu.admin.oas.vo;

import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class MeProfileUpdateRequest {

    /**
     * 昵称
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("nickname")
    private String nickname;
    /**
     * 头像地址
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("avatar")
    private String avatar;
}

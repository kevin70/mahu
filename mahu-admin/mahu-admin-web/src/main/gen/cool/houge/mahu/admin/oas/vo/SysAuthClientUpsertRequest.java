package cool.houge.mahu.admin.oas.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import cool.houge.mahu.admin.oas.vo.TerminalTypeEnum;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysAuthClientUpsertRequest {

    /**
     * 标签
     */
      @NotNull
 @Size(max=128)
    @com.fasterxml.jackson.annotation.JsonProperty("label")
    private String label;
    /**
     * 备注
     */
     @Size(max=512)
    @com.fasterxml.jackson.annotation.JsonProperty("remark")
    private String remark;
    /**
     * Get terminalType
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("terminal_type")
    private TerminalTypeEnum terminalType;
    /**
     * 微信应用 ID
     */
     @Size(max=128)
    @com.fasterxml.jackson.annotation.JsonProperty("wechat_appid")
    private String wechatAppid;
    /**
     * 微信应用密钥
     */
     @Size(max=512)
    @com.fasterxml.jackson.annotation.JsonProperty("wechat_appsecret")
    private String wechatAppsecret;
}

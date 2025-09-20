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
    
    @com.fasterxml.jackson.annotation.JsonProperty("label")
    private String label;
    /**
     * 备注
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("remark")
    private String remark;
    /**
     * Get terminalType
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("terminal_type")
    private TerminalTypeEnum terminalType;
    /**
     * 微信应用 ID
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("wechat_appid")
    private String wechatAppid;
    /**
     * 微信应用密钥
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("wechat_appsecret")
    private String wechatAppsecret;
}


package cool.houge.mahu.admin.oas.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import cool.houge.mahu.admin.oas.vo.TerminalTypeEnum;
import java.time.LocalDateTime;
import io.avaje.validation.constraints.*;

    /**
    * 客户端
    */
@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysAuthClientResponse {

    /**
     * 客户端 ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("client_id")
    private String clientId;
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
     * 客户端密钥
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("client_secret")
    private String clientSecret;
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


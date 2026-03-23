package cool.houge.mahu.admin.oas.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.UUID;
import io.avaje.validation.constraints.*;

    /**
    * 管理员认证记录
    */
@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysAdminAuthLogResponse {

    /**
     * 日志 ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private UUID id;
    /**
     * 创建时间
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private OffsetDateTime createdAt;
    /**
     * 管理员 ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("admin_id")
    private Long adminId;

                /**
                * 认证类型
                */
                public enum GrantTypeEnum {
                    PASSWORD("PASSWORD"),
                    REFRESH_TOKEN("REFRESH_TOKEN");
            
                    private String value;
            
                    GrantTypeEnum(String value) {
                        this.value = value;
                    }
            
                    @JsonValue
                    public String getValue() {
                        return value;
                    }
            
                    @Override
                    public String toString() {
                        return String.valueOf(value);
                    }
            
            
                    @JsonCreator
                    public static GrantTypeEnum fromValue(String text) {
                        for (GrantTypeEnum b : GrantTypeEnum.values()) {
                            if (String.valueOf(b.value).equals(text)) {
                                return b;
                            }
                        }
                        throw new IllegalArgumentException("Unexpected value '" + text + "'");
                    }
                }

    /**
     * 认证类型
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("grant_type")
    private GrantTypeEnum grantType;
    /**
     * 认证客户端 ID
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("client_id")
    private String clientId;
    /**
     * 访问 IP
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("ip_addr")
    private String ipAddr;
    /**
     * HTTP User-Agent
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("user_agent")
    private String userAgent;
}

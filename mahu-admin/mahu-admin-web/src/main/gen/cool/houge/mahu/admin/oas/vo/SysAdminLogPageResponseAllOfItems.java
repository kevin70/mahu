package cool.houge.mahu.admin.oas.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import cool.houge.mahu.admin.oas.vo.SysAdminAccessLogResponse;
import cool.houge.mahu.admin.oas.vo.SysAdminAuthLogResponse;
import cool.houge.mahu.admin.oas.vo.SysAdminChangeLogResponse;
import cool.houge.mahu.admin.oas.vo.SysAdminChangeLogResponseItemsInner;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysAdminLogPageResponseAllOfItems {

    /**
     * 日志 ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private String id;
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
     * 访问 IP
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("ip_addr")
    private String ipAddr;
    /**
     * 访问的 HTTP Method
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("method")
    private String method;
    /**
     * 访问的 HTTP 路径
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("uri_path")
    private String uriPath;
    /**
     * HTTP referer
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("referer")
    private String referer;
    /**
     * 访问协议
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("protocol")
    private String protocol;
    /**
     * 响应状态
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("response_status")
    private Integer responseStatus;
    /**
     * 响应字节数
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("response_bytes")
    private Long responseBytes;
    /**
     * HTTP User-Agent
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("user_agent")
    private String userAgent;

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
     * 操作来源
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("source")
    private String source;
    /**
     * 变更项
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("items")
    private List<@Valid SysAdminChangeLogResponseItemsInner> items;
}

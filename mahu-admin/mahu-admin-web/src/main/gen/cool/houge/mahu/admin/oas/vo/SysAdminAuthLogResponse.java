package cool.houge.mahu.admin.oas.vo;

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
     * 认证方法 - `LOGIN` - `TOKEN_REFRESH` 
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("auth_type")
    private String authType;
    /**
     * 认证方法
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("auth_method")
    private String authMethod;
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

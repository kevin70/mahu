package cool.houge.mahu.admin.oas.vo;

import java.time.OffsetDateTime;
import java.util.UUID;
import io.avaje.validation.constraints.*;

    /**
    * 管理员登录尝试记录
    */
@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysAdminLoginAttemptResponse {

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
     * 命中的管理员 ID
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("admin_id")
    private Long adminId;
    /**
     * 授权类型
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("grant_type")
    private String grantType;
    /**
     * 认证客户端 ID
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("client_id")
    private String clientId;
    /**
     * 登录名快照
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("username")
    private String username;
    /**
     * 是否登录成功
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("success")
    private Boolean success;
    /**
     * 失败原因码
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("reason_code")
    private String reasonCode;
    /**
     * 失败原因摘要
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("reason_detail")
    private String reasonDetail;
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

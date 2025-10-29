package cool.houge.mahu.admin.oas.vo;

import java.time.LocalDateTime;
import io.avaje.validation.constraints.*;

    /**
    * 管理员访问记录
    */
@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysAdminAccessLogResponse {

    /**
     * 日志 ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private Long id;
    /**
     * 创建时间
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private LocalDateTime createdAt;
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
     * 请求的 User Agent
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("user_agent")
    private String userAgent;
}

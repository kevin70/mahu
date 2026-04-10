package cool.houge.mahu.admin.oas.vo;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class MeNotificationResponse {

    /**
     * 通知 ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private Long id;
    /**
     * 通知标题
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("title")
    private String title;
    /**
     * 通知内容
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("content")
    private String content;
    /**
     * 发送范围：1定向，2全局
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("scope")
    private Integer scope;
    /**
     * 通知类型
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("type")
    private Integer type;
    /**
     * 状态：200生效，920过期
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("status")
    private Integer status;
    /**
     * 扩展载荷
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("payload")
    private Map<String, Object> payload;
    /**
     * 过期时间，NULL表示不过期
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("expire_at")
    private OffsetDateTime expireAt;
    /**
     * 创建时间
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private OffsetDateTime createdAt;
    /**
     * 更新时间
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("updated_at")
    private OffsetDateTime updatedAt;
    /**
     * 当前管理员是否已读
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("read")
    private Boolean read;
    /**
     * 已读时间
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("read_at")
    private OffsetDateTime readAt;
}

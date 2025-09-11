package cool.houge.mahu.admin.oas.model;

import java.time.LocalDateTime;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class LogAdminAuditResponse {

    /**
     * 日志 ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private Long id;
    /**
     * 创建时间
     */
      @NotNull

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
     * 表名
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("table_name")
    private String tableName;
    /**
     * 租户 ID
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("data_tenant_id")
    private String dataTenantId;
    /**
     * 修改类型 - `I` 新增 - `U` 修改 - `D` 删除 
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("change_type")
    private String changeType;
    /**
     * 数据 ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("data_id")
    private String dataId;
    /**
     * 新数据
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("data")
    private String data;
    /**
     * 旧数据
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("old_data")
    private String oldData;
}


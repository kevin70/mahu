package cool.houge.mahu.admin.oas.vo;

import cool.houge.mahu.admin.oas.vo.SysAdminChangeLogResponseItemsInner;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.avaje.validation.constraints.*;

    /**
    * 管理员变更记录
    */
@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysAdminChangeLogResponse {

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

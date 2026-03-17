package cool.houge.mahu.admin.oas.vo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysAdminChangeLogResponseItemsInner {

    /**
     * 日志项 ID
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private String id;
    /**
     * 创建时间
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private OffsetDateTime createdAt;
    /**
     * 表名
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("table_name")
    private String tableName;
    /**
     * 租户 ID
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("tenant_id")
    private String tenantId;
    /**
     * 数据 ID
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("data_id")
    private String dataId;
    /**
     * 变更类型
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("change_type")
    private String changeType;
    /**
     * 事件创建时间戳
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("event_time")
    private Long eventTime;
    /**
     * 新数据
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("data")
    private String data;
    /**
     * 旧数据
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("old_data")
    private String oldData;
}

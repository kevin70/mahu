package cool.houge.mahu.admin.oas.vo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class SysAdminChangeLogResponseItemsInner {

    /**
     * 类型
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("type")
    private String type;
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

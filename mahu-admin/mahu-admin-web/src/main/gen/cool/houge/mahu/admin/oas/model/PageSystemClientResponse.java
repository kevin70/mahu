package cool.houge.mahu.admin.oas.model;

import cool.houge.mahu.admin.oas.model.SystemClientResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.avaje.validation.constraints.*;

    /**
    * 客户端列表
    */
@lombok.Data
@io.avaje.validation.constraints.Valid
public class PageSystemClientResponse {

    /**
     * 客户端列表
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("items")
    private List<@Valid SystemClientResponse> items;
    /**
     * 总记录数量
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("total_count")
    private Integer totalCount;
}


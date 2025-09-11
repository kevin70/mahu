package cool.houge.mahu.admin.oas.model;

import cool.houge.mahu.admin.oas.model.SystemDictTypeResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class PageSystemDictResponse {

    /**
     * 品牌列表
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("items")
    private List<@Valid SystemDictTypeResponse> items;
    /**
     * 总记录数量
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("total_count")
    private Integer totalCount;
}


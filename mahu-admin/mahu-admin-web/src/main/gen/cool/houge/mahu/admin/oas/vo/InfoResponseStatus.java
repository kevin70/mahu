package cool.houge.mahu.admin.oas.vo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import io.avaje.validation.constraints.*;

    /**
    * 应用状态
    */
@lombok.Data
@io.avaje.validation.constraints.Valid
public class InfoResponseStatus {

    /**
     * 应用状态
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("overall")
    private String overall;
    /**
     * 应用启动时间
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("uptime")
    private String uptime;
    /**
     * 组件状态
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("components")
    private List<Map<String, Object>> components;
}


package cool.houge.mahu.admin.oas.vo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class ErrorResponseError {

    /**
     * 日志追踪 ID
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("trace_id")
    private String traceId;
    /**
     * 时间戳
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("timestamp")
    private LocalDateTime timestamp;
    /**
     * 请求路径
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("path")
    private String path;
    /**
     * HTTP Method
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("method")
    private String method;
    /**
     * HTTP Status
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("status")
    private Integer status;
    /**
     * 错误代码
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("code")
    private Integer code;
    /**
     * 错误描述
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("message")
    private String message;
    /**
     * 详细信息
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("details")
    private Map<String, Object> details;
    /**
     * 错误堆栈，该字段仅在测试环境返回
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("stacktrace")
    private List<String> stacktrace;
}

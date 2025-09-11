package cool.houge.mahu.admin.oas.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import cool.houge.mahu.admin.oas.model.ErrorResponseErrorInvalidParamsInner;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
     * 子错误码
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("sub_code")
    private String subCode;
    /**
     * 错误描述
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("message")
    private String message;
    /**
     * 详细信息
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("detail")
    private String detail;
    /**
     * 调试堆栈，此字段仅在请求 URL 中存在 `debug` 查询参数时返回 
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("stacktrace")
    private List<String> stacktrace;
    /**
     * 非法参数
     */
    
    @com.fasterxml.jackson.annotation.JsonProperty("invalid_params")
    private List<@Valid ErrorResponseErrorInvalidParamsInner> invalidParams;
}


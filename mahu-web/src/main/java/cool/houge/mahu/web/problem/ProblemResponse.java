package cool.houge.mahu.web.problem;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.Data;

/// 错误响应
///
/// @author ZY (kzou227@qq.com)
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProblemResponse {

    /// 日志追踪
    @JsonProperty("trace_id")
    private String traceId;
    /// 时间戳
    private OffsetDateTime timestamp;
    /// 请求路径
    private String path;
    /// 请求 HTTP 方法
    private String method;

    /// 响应 HTTP 状态
    private Integer status;
    /// 错误代码
    private Integer code;
    /// 错误描述
    private String message;
    /// 详细信息
    @JsonUnwrapped
    private Map<String, Object> details;
    /// 异常堆栈
    private List<String> stacktrace;
}

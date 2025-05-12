package cool.houge.mahu.web;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

/// 错误响应
///
/// @author ZY (kzou227@qq.com)
@Data
public class ErrorResponse {

    /// 错误响应
    Error error;

    @Data
    public static class Error {
        /// 日志追踪 ID
        String traceId;
        /// 时间戳
        OffsetDateTime timestamp;
        /// 请求路径
        String path;
        /// HTTP Method
        String method;
        /// HTTP Status
        int status;
        /// 错误码
        int code;
        /// 子错误码
        String subCode;
        /// 错误描述
        String message;
        /// 详细信息
        String detail;
        /// 调试堆栈，此字段仅在请求 URL 中存在 `debug` 查询参数时返回
        List<String> stacktrace;
        /// 非法参数
        List<InvalidParam> invalidParams;
    }

    /// 无效的参数
    ///
    /// @param propertyName 属性名称
    /// @param message      错误描述
    public record InvalidParam(String propertyName, String message) {}
}

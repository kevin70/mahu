package cool.houge.mahu.web.problem;

/// API 错误响应处理器契约。
///
/// `exceptionType` 是 `RestErrorHandler` 构建索引与层级匹配的关键元信息，
/// 不应随意移除，否则会退化为遍历匹配。
///
/// @author ZY (kzou227@qq.com)
public interface ProblemHandler {

    /// 将已匹配的异常转换为统一错误语义。
    ProblemSpec handle(Throwable ex);

    /// 当前处理器负责的异常根类型（用于索引与匹配，不用于日志展示）。
    Class<? extends Throwable> exceptionType();
}

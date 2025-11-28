package cool.houge.mahu.admin.problem;

/// API 错误响应
///
/// @author ZY (kzou227@qq.com)
public interface ProblemHandler {

    /// 是否能处理响应
    boolean canHandle(Throwable e);

    /// 处理响应
    ProblemResponse handle(Throwable ex);
}

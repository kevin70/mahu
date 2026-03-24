/**
 * 本包中的异常处理器仅用于 {@link cool.houge.mahu.web.problem.RestErrorHandler} 内部装配。
 *
 * <p>业务代码不得直接引用、实例化或调用本包中的 {@code *ExceptionHandler}。新增处理器需要通过
 * {@code RestErrorHandler} 统一注册，以确保匹配策略、日志策略与错误响应结构保持一致。
 */
package cool.houge.mahu.web.problem.handler;

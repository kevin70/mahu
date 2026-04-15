package cool.houge.mahu.web.problem;

import cool.houge.mahu.BizCodes;
import cool.houge.mahu.web.problem.handler.BizCodeExceptionHandler;
import cool.houge.mahu.web.problem.handler.ConstraintViolationExceptionHandler;
import cool.houge.mahu.web.problem.handler.DuplicateKeyExceptionHandler;
import cool.houge.mahu.web.problem.handler.EntityNotFoundExceptionHandler;
import cool.houge.mahu.web.problem.handler.ForbiddenExceptionHandler;
import cool.houge.mahu.web.problem.handler.HttpExceptionHandler;
import cool.houge.mahu.web.problem.handler.NotFoundExceptionHandler;
import cool.houge.mahu.web.problem.handler.UnsupportedTypeExceptionHandler;
import io.helidon.webserver.http.ErrorHandler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;

/// 错误响应处理器。
///
/// `problem.handler` 包下的 `*ExceptionHandler` 仅供本类装配使用，不应在业务代码中直接调用。
/// 新增异常处理器必须通过本类统一注册，以保证匹配优先级、日志与响应结构一致性。
///
/// @author ZY (kzou227@qq.com)
public class RestErrorHandler implements ErrorHandler<Throwable> {

    private static final Logger log = LogManager.getLogger(RestErrorHandler.class);
    /// 未命中哨兵，避免对同一异常类型重复执行层级遍历。
    private static final ProblemHandler NO_HANDLER = new MissingProblemHandler();
    private final ProblemResponseFactory problemResponseFactory;
    /// 精确类型索引：异常类 -> 处理器，命中路径为 O(1)。
    private final Map<Class<? extends Throwable>, ProblemHandler> handlerIndex;

    public RestErrorHandler() {
        this.problemResponseFactory = new ProblemResponseFactory();
        this.handlerIndex = buildIndex(buildDefaultHandlers());
    }

    public RestErrorHandler(List<ProblemHandler> problemHandlers, ProblemResponseFactory problemResponseFactory) {
        this.problemResponseFactory = problemResponseFactory;
        this.handlerIndex = buildIndex(mergeWithDefaultHandlers(problemHandlers));
    }

    private static List<ProblemHandler> mergeWithDefaultHandlers(List<ProblemHandler> problemHandlers) {
        var merged = new ArrayList<ProblemHandler>();
        // 传入处理器优先，便于在不修改默认链路的前提下覆盖同类型处理。
        merged.addAll(problemHandlers);
        merged.addAll(buildDefaultHandlers());
        return List.copyOf(merged);
    }

    private static List<ProblemHandler> buildDefaultHandlers() {
        var handlers = new ArrayList<ProblemHandler>();
        handlers.add(new BizCodeExceptionHandler());
        handlers.add(new HttpExceptionHandler());
        addIfClassPresent(handlers, "io.helidon.http.ForbiddenException", ForbiddenExceptionHandler::new);
        addIfClassPresent(handlers, "io.helidon.http.NotFoundException", NotFoundExceptionHandler::new);
        // 可选依赖相关处理器按“异常类是否存在”动态装配，避免启动期类加载失败。
        addIfClassPresent(
                handlers, "io.avaje.validation.ConstraintViolationException", ConstraintViolationExceptionHandler::new);
        addIfClassPresent(handlers, "io.ebean.DuplicateKeyException", DuplicateKeyExceptionHandler::new);
        addIfClassPresent(handlers, "jakarta.persistence.EntityNotFoundException", EntityNotFoundExceptionHandler::new);
        addIfClassPresent(
                handlers, "io.helidon.http.media.UnsupportedTypeException", UnsupportedTypeExceptionHandler::new);
        return List.copyOf(handlers);
    }

    private static void addIfClassPresent(
            List<ProblemHandler> handlers, String exceptionClassName, Supplier<ProblemHandler> handlerSupplier) {
        if (classPresent(exceptionClassName)) {
            handlers.add(handlerSupplier.get());
        }
    }

    private static boolean classPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException _) {
            return false;
        }
    }

    @Override
    public void handle(ServerRequest req, ServerResponse res, @NonNull Throwable ex) {
        var spec = resolveProblemSpec(ex);
        var err = problemResponseFactory.from(req, ex, spec);

        logProblem(req, err, ex);
        res.status(err.getStatus()).send(Map.of("error", err));
    }

    private Map<Class<? extends Throwable>, ProblemHandler> buildIndex(List<ProblemHandler> problemHandlers) {
        var index = new HashMap<Class<? extends Throwable>, ProblemHandler>();
        for (var handler : problemHandlers) {
            index.putIfAbsent(handler.exceptionType(), handler);
        }
        return Map.copyOf(index);
    }

    private ProblemSpec resolveProblemSpec(Throwable ex) {
        Throwable current = ex;
        while (current != null) {
            // 仅做精确类型匹配（不回退父类/接口）。
            var handler = handlerIndex.get(current.getClass());
            if (handler != null) {
                return handler.handle(current);
            }
            current = current.getCause();
        }
        return NO_HANDLER.handle(ex);
    }

    private void logProblem(ServerRequest req, ProblemResponse err, Throwable ex) {
        var method = req.prologue().method().text();
        var path = req.path().rawPath();
        var status = err.getStatus();

        // 5xx 记录完整异常堆栈；4xx 仅记录关键信息降低日志噪音。
        if (status >= 500) {
            log.error(
                    "request_failed method={} path={} status={} code={} message={}",
                    method,
                    path,
                    status,
                    err.getCode(),
                    err.getMessage(),
                    ex);
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug(
                    "request_rejected method={} path={} status={} code={} message={}",
                    method,
                    path,
                    status,
                    err.getCode(),
                    err.getMessage(),
                    ex);
        }
    }

    private static final class MissingProblemHandler implements ProblemHandler {
        @Override
        public ProblemSpec handle(Throwable ex) {
            return new ProblemSpec()
                    .setStatus(500)
                    .setCode(BizCodes.INTERNAL.code())
                    .setMessage(Objects.requireNonNullElse(ex.getMessage(), BizCodes.INTERNAL.message()));
        }

        @Override
        public Class<? extends Throwable> exceptionType() {
            return Throwable.class;
        }
    }
}

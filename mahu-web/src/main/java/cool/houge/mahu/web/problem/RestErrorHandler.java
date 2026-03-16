package cool.houge.mahu.web.problem;

import cool.houge.mahu.BizCodes;
import cool.houge.mahu.Env;
import cool.houge.mahu.util.Metadata;
import cool.houge.mahu.web.problem.handler.BizCodeExceptionHandler;
import cool.houge.mahu.web.problem.handler.ConstraintViolationExceptionHandler;
import cool.houge.mahu.web.problem.handler.DuplicateKeyExceptionHandler;
import cool.houge.mahu.web.problem.handler.EntityNotFoundExceptionHandler;
import cool.houge.mahu.web.problem.handler.HttpExceptionHandler;
import cool.houge.mahu.web.problem.handler.UnsupportedTypeExceptionHandler;
import io.helidon.webserver.http.ErrorHandler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;

/// 错误响应处理器
///
/// @author ZY (kzou227@qq.com)
@AllArgsConstructor
public class RestErrorHandler implements ErrorHandler<Throwable> {

    private static final Logger log = LogManager.getLogger(RestErrorHandler.class);
    private final List<ProblemHandler> problemHandlers;

    public RestErrorHandler() {
        this(List.of(
                new BizCodeExceptionHandler(),
                new ConstraintViolationExceptionHandler(),
                new DuplicateKeyExceptionHandler(),
                new EntityNotFoundExceptionHandler(),
                new HttpExceptionHandler(),
                new UnsupportedTypeExceptionHandler()
                //
                ));
    }

    @Override
    public void handle(ServerRequest req, ServerResponse res, @NonNull Throwable ex) {
        ProblemResponse errorResponse = null;
        Throwable current = ex;
        while (current != null && errorResponse == null) {
            for (var h : problemHandlers) {
                if (h.canHandle(current)) {
                    errorResponse = h.handle(current);
                    break;
                }
            }
            if (errorResponse == null) {
                current = current.getCause();
            }
        }

        if (errorResponse == null) {
            errorResponse = new ProblemResponse()
                    .setStatus(500)
                    .setCode(BizCodes.INTERNAL.code())
                    .setMessage(Objects.requireNonNullElse(ex.getMessage(), BizCodes.INTERNAL.message()));
        }

        send0(req, res, ex, errorResponse);
    }

    private void send0(ServerRequest req, ServerResponse res, Throwable ex, ProblemResponse err) {
        var metadata = Metadata.current();
        err.setTraceId(metadata.traceId())
                .setTimestamp(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .setPath(req.path().rawPath())
                .setMethod(req.prologue().method().text());

        if (!Env.current().isProd()) {
            var list = Arrays.stream(ex.getStackTrace())
                    .map(StackTraceElement::toString)
                    .toList();
            err.setStacktrace(list);
        }

        // 记录错误日志
        if (err.getStatus() >= 500) {
            log.error("服务器错误 {}", err, ex);
        } else {
            log.debug("{}", err, ex);
        }
        res.status(err.getStatus()).send(Map.of("error", err));
    }
}

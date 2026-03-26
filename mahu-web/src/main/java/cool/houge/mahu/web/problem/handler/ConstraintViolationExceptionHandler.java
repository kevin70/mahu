package cool.houge.mahu.web.problem.handler;

import static cool.houge.mahu.BizCodes.INVALID_ARGUMENT;

import cool.houge.mahu.web.problem.ProblemHandler;
import cool.houge.mahu.web.problem.ProblemSpec;
import io.avaje.validation.ConstraintViolationException;
import io.helidon.http.Status;
import java.util.Map;

/// [io.avaje.validation.ConstraintViolationException]
///
/// @author ZY (kzou227@qq.com)
public class ConstraintViolationExceptionHandler implements ProblemHandler {

    @Override
    public Class<? extends Throwable> exceptionType() {
        return ConstraintViolationException.class;
    }

    @Override
    public ProblemSpec handle(Throwable ex) {
        var e = (ConstraintViolationException) ex;
        var details = Map.<String, Object>of(
                "invalid_params",
                e.violations().stream()
                        .map(o -> Map.of("path", o.path(), "field", o.field(), "message", o.message()))
                        .toList());
        return new ProblemSpec()
                .setStatus(Status.BAD_REQUEST_400.code())
                .setCode(INVALID_ARGUMENT.code())
                .setMessage(INVALID_ARGUMENT.message())
                .setDetails(details);
    }
}

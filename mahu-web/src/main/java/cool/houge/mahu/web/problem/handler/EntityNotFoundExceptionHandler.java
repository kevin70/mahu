package cool.houge.mahu.web.problem.handler;

import cool.houge.mahu.BizCodes;
import cool.houge.mahu.web.problem.ProblemHandler;
import cool.houge.mahu.web.problem.ProblemSpec;
import io.helidon.http.Status;
import jakarta.persistence.EntityNotFoundException;
import java.util.Map;

/// [jakarta.persistence.EntityNotFoundException]
///
/// @author ZY (kzou227@qq.com)
public class EntityNotFoundExceptionHandler implements ProblemHandler {

    @Override
    public Class<? extends Throwable> exceptionType() {
        return EntityNotFoundException.class;
    }

    @Override
    public ProblemSpec handle(Throwable ex) {
        var e = (EntityNotFoundException) ex;
        return new ProblemSpec()
                .setStatus(Status.NOT_FOUND_404.code())
                .setCode(BizCodes.NOT_FOUND.code())
                .setMessage(BizCodes.NOT_FOUND.message())
                .setDetails(Map.of(
                        "exception", e.getClass().getName(), "exception_message", String.valueOf(e.getMessage())));
    }
}

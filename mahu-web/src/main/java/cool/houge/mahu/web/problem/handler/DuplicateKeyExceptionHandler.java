package cool.houge.mahu.web.problem.handler;

import cool.houge.mahu.BizCodes;
import cool.houge.mahu.web.problem.ProblemHandler;
import cool.houge.mahu.web.problem.ProblemSpec;
import io.ebean.DuplicateKeyException;
import io.helidon.http.Status;
import java.util.Map;

/// [io.ebean.DuplicateKeyException]
///
/// @author ZY (kzou227@qq.com)
public class DuplicateKeyExceptionHandler implements ProblemHandler {

    @Override
    public Class<? extends Throwable> exceptionType() {
        return DuplicateKeyException.class;
    }

    @Override
    public ProblemSpec handle(Throwable ex) {
        var e = (DuplicateKeyException) ex;
        return new ProblemSpec()
                .setStatus(Status.CONFLICT_409.code())
                .setCode(BizCodes.ALREADY_EXISTS.code())
                .setMessage(BizCodes.ALREADY_EXISTS.message())
                .setDetails(Map.of(
                        "exception", e.getClass().getName(), "exception_message", String.valueOf(e.getMessage())));
    }
}

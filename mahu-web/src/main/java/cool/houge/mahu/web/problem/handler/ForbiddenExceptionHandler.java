package cool.houge.mahu.web.problem.handler;

import cool.houge.mahu.BizCodes;
import cool.houge.mahu.web.problem.ProblemHandler;
import cool.houge.mahu.web.problem.ProblemSpec;
import io.helidon.http.ForbiddenException;

/// [io.helidon.http.ForbiddenException]
public class ForbiddenExceptionHandler implements ProblemHandler {

    @Override
    public Class<? extends Throwable> exceptionType() {
        return ForbiddenException.class;
    }

    @Override
    public ProblemSpec handle(Throwable ex) {
        var e = (ForbiddenException) ex;
        return new ProblemSpec()
                .setStatus(e.status().code())
                .setCode(BizCodes.PERMISSION_DENIED.code())
                .setMessage(e.getMessage());
    }
}

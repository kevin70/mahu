package cool.houge.mahu.web.problem.handler;

import cool.houge.mahu.BizCodes;
import cool.houge.mahu.web.problem.ProblemHandler;
import cool.houge.mahu.web.problem.ProblemSpec;
import io.helidon.http.NotFoundException;

/// [io.helidon.http.NotFoundException]
///
/// @author ZY (kzou227@qq.com)
public class NotFoundExceptionHandler implements ProblemHandler {

    @Override
    public Class<? extends Throwable> exceptionType() {
        return NotFoundException.class;
    }

    @Override
    public ProblemSpec handle(Throwable ex) {
        var e = (NotFoundException) ex;
        return new ProblemSpec()
                .setStatus(e.status().code())
                .setCode(BizCodes.NOT_FOUND.code())
                .setMessage(e.getMessage());
    }
}

package cool.houge.mahu.web.problem.handler;

import cool.houge.mahu.BizCodes;
import cool.houge.mahu.web.problem.ProblemHandler;
import cool.houge.mahu.web.problem.ProblemResponse;
import io.ebean.DuplicateKeyException;
import io.helidon.http.Status;

/// [io.ebean.DuplicateKeyException]
///
/// @author ZY (kzou227@qq.com)
public class DuplicateKeyExceptionHandler implements ProblemHandler {

    @Override
    public boolean canHandle(Throwable e) {
        return e instanceof DuplicateKeyException;
    }

    @Override
    public ProblemResponse handle(Throwable ex) {
        return new ProblemResponse()
                .setStatus(Status.CONFLICT_409_CODE)
                .setCode(BizCodes.ALREADY_EXISTS.code())
                .setMessage(BizCodes.ALREADY_EXISTS.message());
    }
}

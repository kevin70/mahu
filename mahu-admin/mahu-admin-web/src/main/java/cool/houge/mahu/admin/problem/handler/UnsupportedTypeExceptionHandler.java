package cool.houge.mahu.admin.problem.handler;

import cool.houge.mahu.admin.problem.ProblemHandler;
import cool.houge.mahu.admin.problem.ProblemResponse;
import io.helidon.http.Status;
import io.helidon.http.media.UnsupportedTypeException;

/// [io.helidon.http.media.UnsupportedTypeException]
///
/// @author ZY (kzou227@qq.com)
public class UnsupportedTypeExceptionHandler implements ProblemHandler {

    @Override
    public boolean canHandle(Throwable e) {
        return e instanceof UnsupportedTypeException;
    }

    @Override
    public ProblemResponse handle(Throwable ex) {
        return new ProblemResponse()
                .setStatus(Status.UNSUPPORTED_MEDIA_TYPE_415.code())
                .setCode(Status.UNSUPPORTED_MEDIA_TYPE_415.code())
                .setMessage(ex.getMessage());
    }
}

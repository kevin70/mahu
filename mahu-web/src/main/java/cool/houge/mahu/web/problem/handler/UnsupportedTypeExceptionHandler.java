package cool.houge.mahu.web.problem.handler;

import cool.houge.mahu.BizCodes;
import cool.houge.mahu.web.problem.ProblemHandler;
import cool.houge.mahu.web.problem.ProblemSpec;
import io.helidon.http.Status;
import io.helidon.http.media.UnsupportedTypeException;
import java.util.Map;

/// [io.helidon.http.media.UnsupportedTypeException]
///
/// @author ZY (kzou227@qq.com)
public class UnsupportedTypeExceptionHandler implements ProblemHandler {

    @Override
    public Class<? extends Throwable> exceptionType() {
        return UnsupportedTypeException.class;
    }

    @Override
    public ProblemSpec handle(Throwable ex) {
        var e = (UnsupportedTypeException) ex;
        return new ProblemSpec()
                .setStatus(Status.UNSUPPORTED_MEDIA_TYPE_415.code())
                .setCode(BizCodes.INVALID_ARGUMENT.code())
                .setMessage(BizCodes.INVALID_ARGUMENT.message())
                .setDetails(Map.of(
                        "exception",
                        e.getClass().getName(),
                        "exception_message",
                        String.valueOf(e.getMessage()),
                        "http_status",
                        Status.UNSUPPORTED_MEDIA_TYPE_415.code()));
    }
}

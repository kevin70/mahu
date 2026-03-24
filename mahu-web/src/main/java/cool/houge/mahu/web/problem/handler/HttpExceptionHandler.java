package cool.houge.mahu.web.problem.handler;

import cool.houge.mahu.BizCodes;
import cool.houge.mahu.web.problem.ProblemHandler;
import cool.houge.mahu.web.problem.ProblemSpec;
import io.helidon.http.HttpException;
import java.util.Map;

/// [io.helidon.http.HttpException]
///
/// @author ZY (kzou227@qq.com)
public class HttpExceptionHandler implements ProblemHandler {

    @Override
    public Class<? extends Throwable> exceptionType() {
        return HttpException.class;
    }

    @Override
    public ProblemSpec handle(Throwable ex) {
        var e = (HttpException) ex;
        var status = e.status().code();
        var bz = mapBizCode(status);
        return new ProblemSpec()
                .setStatus(status)
                .setCode(bz.code())
                .setMessage(bz.message())
                .setDetails(Map.of(
                        "exception", e.getClass().getName(),
                        "exception_message", String.valueOf(e.getMessage()),
                        "http_status", status));
    }

    private BizCodes mapBizCode(int status) {
        return switch (status) {
            case 400, 405, 406 -> BizCodes.INVALID_ARGUMENT;
            case 401 -> BizCodes.UNAUTHENTICATED;
            case 403 -> BizCodes.PERMISSION_DENIED;
            case 404 -> BizCodes.NOT_FOUND;
            case 409 -> BizCodes.ALREADY_EXISTS;
            case 412 -> BizCodes.FAILED_PRECONDITION;
            case 415, 422 -> BizCodes.INVALID_ARGUMENT;
            case 429 -> BizCodes.RESOURCE_EXHAUSTED;
            case 501 -> BizCodes.UNIMPLEMENTED;
            case 503 -> BizCodes.UNAVAILABLE;
            default -> BizCodes.INTERNAL;
        };
    }
}

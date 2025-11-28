package cool.houge.mahu.web.problem.handler;

import static cool.houge.mahu.BizCodes.ALREADY_EXISTS;
import static cool.houge.mahu.BizCodes.DEADLINE_EXCEEDED;
import static cool.houge.mahu.BizCodes.FAILED_PRECONDITION;
import static cool.houge.mahu.BizCodes.INVALID_ARGUMENT;
import static cool.houge.mahu.BizCodes.NOT_FOUND;
import static cool.houge.mahu.BizCodes.OUT_OF_RANGE;
import static cool.houge.mahu.BizCodes.PERMISSION_DENIED;
import static cool.houge.mahu.BizCodes.UNAUTHENTICATED;
import static cool.houge.mahu.BizCodes.UNAVAILABLE;
import static cool.houge.mahu.BizCodes.UNIMPLEMENTED;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.web.problem.ProblemHandler;
import cool.houge.mahu.web.problem.ProblemResponse;
import io.helidon.http.Status;

/// [cool.houge.mahu.BizCodeException]
///
/// @author ZY (kzou227@qq.com)
public class BizCodeExceptionHandler implements ProblemHandler {

    @Override
    public boolean canHandle(Throwable e) {
        return e instanceof BizCodeException;
    }

    @Override
    public ProblemResponse handle(Throwable ex) {
        var bz = ((BizCodeException) ex).getCode();
        // 状态码映射
        Status status =
                switch (bz) {
                    case INVALID_ARGUMENT, DEADLINE_EXCEEDED, OUT_OF_RANGE, UNIMPLEMENTED -> Status.BAD_REQUEST_400;
                    case UNAUTHENTICATED -> Status.UNAUTHORIZED_401;
                    case FAILED_PRECONDITION, PERMISSION_DENIED -> Status.FORBIDDEN_403;
                    case NOT_FOUND -> Status.NOT_FOUND_404;
                    case ALREADY_EXISTS -> Status.CONFLICT_409;
                    case UNAVAILABLE -> Status.SERVICE_UNAVAILABLE_503;
                    default -> Status.INTERNAL_SERVER_ERROR_500;
                };
        return new ProblemResponse().setStatus(status.code()).setCode(bz.code()).setMessage(bz.message());
    }
}

package cool.houge.mahu.admin.problem.handler;

import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.problem.ProblemHandler;
import cool.houge.mahu.admin.problem.ProblemResponse;
import io.helidon.http.Status;
import jakarta.persistence.EntityNotFoundException;

/// [jakarta.persistence.EntityNotFoundException]
///
/// @author ZY (kzou227@qq.com)
public class EntityNotFoundExceptionHandler implements ProblemHandler {

    @Override
    public boolean canHandle(Throwable e) {
        return e instanceof EntityNotFoundException;
    }

    @Override
    public ProblemResponse handle(Throwable ex) {
        return new ProblemResponse()
                .setStatus(Status.NOT_FOUND_404.code())
                .setCode(BizCodes.NOT_FOUND.code())
                .setMessage(ex.getMessage());
    }
}

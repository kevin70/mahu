package cool.houge.mahu.admin.problem.handler;

import cool.houge.mahu.admin.problem.ProblemHandler;
import cool.houge.mahu.admin.problem.ProblemResponse;
import io.helidon.http.HttpException;

/// [io.helidon.http.HttpException]
///
/// @author ZY (kzou227@qq.com)
public class HttpExceptionHandler implements ProblemHandler {

    @Override
    public boolean canHandle(Throwable e) {
        return e instanceof HttpException;
    }

    @Override
    public ProblemResponse handle(Throwable ex) {
        var e = (HttpException) ex;
        return new ProblemResponse()
                .setStatus(e.status().code())
                .setCode(e.status().code())
                .setMessage(e.getMessage());
    }
}

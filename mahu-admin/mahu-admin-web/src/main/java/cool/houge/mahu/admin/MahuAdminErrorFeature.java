package cool.houge.mahu.admin;

import cool.houge.mahu.common.BizCodeException;
import cool.houge.mahu.common.BizCodes;
import cool.houge.mahu.common.Metadata;
import cool.houge.mahu.common.web.ErrorResponse;
import io.avaje.validation.ConstraintViolationException;
import io.ebean.DuplicateKeyException;
import io.helidon.http.HeaderNames;
import io.helidon.http.HttpException;
import io.helidon.http.Status;
import io.helidon.webserver.http.HttpFeature;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static cool.houge.mahu.common.BizCodes.*;


/// 错误处理
///
/// @author ZY (kzou227@qq.com)
public class MahuAdminErrorFeature implements HttpFeature {

    private static final Logger log = LoggerFactory.getLogger(MahuAdminErrorFeature.class);

    @Override
    public void setup(HttpRouting.Builder routing) {
        routing.error(ConstraintViolationException.class, this::handleConstraintViolationException);
        routing.error(EntityNotFoundException.class, this::handleEntityNotFoundException);
        routing.error(DuplicateKeyException.class, this::handleDuplicateKeyException);
        routing.error(BizCodeException.class, this::handleBizCodeException);

        routing.error(HttpException.class, this::handleHttpException);
        routing.error(Throwable.class, this::handleException);
    }

    void handleException(ServerRequest request, ServerResponse response, Throwable cause) {
        var error = new ErrorResponse.Error();
        error.setStatus(Status.INTERNAL_SERVER_ERROR_500.code())
                .setCode(String.valueOf(BizCodes.INTERNAL.code()))
                .setMessage(BizCodes.INTERNAL.message());
        this.send(request, response, error, cause);
    }

    void handleHttpException(ServerRequest request, ServerResponse response, HttpException e) {
        var error = new ErrorResponse.Error();
        error.setStatus(e.status().code())
                .setCode(String.valueOf(e.status().code()))
                .setMessage(e.getMessage());
        this.send(request, response, error, e);
    }

    void handleConstraintViolationException(
            ServerRequest request, ServerResponse response, ConstraintViolationException e) {
        var violations = e.violations().stream()
                .map(cv -> new ErrorResponse.InvalidParam(cv.field(), cv.message()))
                .toList();

        var error = new ErrorResponse.Error();
        error.setStatus(Status.BAD_REQUEST_400.code())
                .setCode(String.valueOf(INVALID_ARGUMENT.code()))
                .setMessage(INVALID_ARGUMENT.message())
                .setInvalidParams(violations);
        this.send(request, response, error, e);
    }

    void handleEntityNotFoundException(ServerRequest request, ServerResponse response, EntityNotFoundException e) {
        var error = new ErrorResponse.Error();
        error.setStatus(Status.NOT_FOUND_404.code())
                .setCode(String.valueOf(BizCodes.NOT_FOUND.code()))
                .setMessage(BizCodes.NOT_FOUND.message());
        this.send(request, response, error, e);
    }

    void handleDuplicateKeyException(ServerRequest request, ServerResponse response, DuplicateKeyException e) {
        var error = new ErrorResponse.Error();
        error.setStatus(Status.CONFLICT_409.code()).setMessage(ALREADY_EXISTS.message());
        this.send(request, response, error, e);
    }

    void handleBizCodeException(ServerRequest request, ServerResponse response, BizCodeException e) {
        var bz = e.getCode();
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

        var error = new ErrorResponse.Error();
        error.setStatus(status.code()).setCode(String.valueOf(bz.code())).setMessage(bz.message());
        this.send(request, response, error, e);
    }

    void send(ServerRequest request, ServerResponse response, ErrorResponse.Error error, Throwable cause) {
        // 通用响应
        var metadata = request.context().get(Metadata.class).orElseThrow();
        error.setTraceId(metadata.traceId())
                .setTimestamp(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .setPath(request.path().rawPath())
                .setMethod(request.prologue().method().text())
                .setDetail(cause.getMessage());

        if (request.query().contains("debug")) {
            var list = Arrays.stream(cause.getStackTrace())
                    .map(StackTraceElement::toString)
                    .toList();
            error.setStacktrace(list);
        }

        // 记录错误日志
        if (error.getStatus() >= 500) {
            log.error("{}", error, cause);
        } else {
            log.debug("{}", error);
        }

        response.header(HeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
                .header(HeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .status(error.getStatus())
                .send(new ErrorResponse().setError(error));
    }
}

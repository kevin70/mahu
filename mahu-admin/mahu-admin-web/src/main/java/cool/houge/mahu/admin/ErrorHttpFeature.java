package cool.houge.mahu.admin;

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
import static java.util.Optional.ofNullable;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.util.Metadata;
import cool.houge.mahu.web.ErrorResponse;
import io.avaje.validation.ConstraintViolationException;
import io.ebean.DuplicateKeyException;
import io.helidon.http.HeaderNames;
import io.helidon.http.HttpException;
import io.helidon.http.Status;
import io.helidon.http.media.UnsupportedTypeException;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.HttpFeature;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 错误处理
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ErrorHttpFeature implements HttpFeature {

    private static final Logger log = LogManager.getLogger();

    @Override
    public void setup(HttpRouting.Builder routing) {
        routing.error(ConstraintViolationException.class, this::handleConstraintViolationException);
        routing.error(EntityNotFoundException.class, this::handleEntityNotFoundException);
        routing.error(DuplicateKeyException.class, this::handleDuplicateKeyException);
        routing.error(BizCodeException.class, this::handleBizCodeException);

        routing.error(UnsupportedTypeException.class, this::handleUnsupportedTypeException);
        routing.error(HttpException.class, this::handleHttpException);
        routing.error(Throwable.class, this::handleException);
    }

    void handleException(ServerRequest request, ServerResponse response, Throwable cause) {
        var error = new ErrorResponse.Error();
        error.setStatus(Status.INTERNAL_SERVER_ERROR_500.code())
            .setCode(BizCodes.INTERNAL.code())
            .setMessage(BizCodes.INTERNAL.message());
        this.send(request, response, error, cause);
    }

    private void handleUnsupportedTypeException(
        ServerRequest request, ServerResponse response, UnsupportedTypeException e) {
        var error = new ErrorResponse.Error();
        error.setStatus(Status.UNSUPPORTED_MEDIA_TYPE_415.code())
            .setCode(Status.UNSUPPORTED_MEDIA_TYPE_415.code())
            .setMessage("请求头 Content-Type 不正确");
        this.send(request, response, error, e);
    }

    void handleHttpException(ServerRequest request, ServerResponse response, HttpException e) {
        var error = new ErrorResponse.Error();
        error.setStatus(e.status().code()).setCode(e.status().code()).setMessage(e.getMessage());
        this.send(request, response, error, e);
    }

    void handleConstraintViolationException(
        ServerRequest request, ServerResponse response, ConstraintViolationException e) {
        var violations = e.violations().stream()
            .map(cv -> new ErrorResponse.InvalidParam(cv.field(), cv.message()))
            .toList();

        var error = new ErrorResponse.Error();
        error.setStatus(Status.BAD_REQUEST_400.code())
            .setCode(INVALID_ARGUMENT.code())
            .setMessage(INVALID_ARGUMENT.message())
            .setInvalidParams(violations);
        this.send(request, response, error, e);
    }

    void handleEntityNotFoundException(ServerRequest request, ServerResponse response, EntityNotFoundException e) {
        var error = new ErrorResponse.Error();
        error.setStatus(Status.NOT_FOUND_404.code())
            .setCode(BizCodes.NOT_FOUND.code())
            .setMessage(e.getMessage());
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
        error.setStatus(status.code())
            .setCode(bz.code())
            .setMessage(ofNullable(e.getRawMessage()).orElse(bz.message()));
        bz.subcode().ifPresent(error::setSubCode);
        this.send(request, response, error, e);
    }

    void send(ServerRequest request, ServerResponse response, ErrorResponse.Error error, Throwable cause) {
        var metadata = Metadata.current();
        error.setTraceId(metadata.traceId())
            .setTimestamp(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString())
            .setPath(request.path().rawPath())
            .setMethod(request.prologue().method().text());

        if (request.query().contains("debug")) {
            var list = Arrays.stream(cause.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
            error.setStacktrace(list);
        }

        // 记录错误日志
        if (error.getStatus() >= 500) {
            log.error("服务器错误 {}", error, cause);
        } else {
            log.debug("{}", error, cause);
        }

        // CORS 请求
        if (request.headers().first(HeaderNames.ACCESS_CONTROL_REQUEST_METHOD).isEmpty()) {
            response.header(HeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
                .header(HeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .header(HeaderNames.VARY, HeaderNames.ORIGIN.toString());
        }
        response.status(error.getStatus()).send(new ErrorResponse().setError(error));
    }
}

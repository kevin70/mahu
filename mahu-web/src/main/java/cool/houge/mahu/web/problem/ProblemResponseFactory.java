package cool.houge.mahu.web.problem;

import cool.houge.mahu.Env;
import cool.houge.mahu.util.Metadata;
import io.helidon.webserver.http.ServerRequest;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/// 统一构建错误响应
///
/// @author ZY (kzou227@qq.com)
public final class ProblemResponseFactory {

    public ProblemResponse from(ServerRequest req, Throwable ex, ProblemSpec spec) {
        var metadata = Metadata.current();
        var response = new ProblemResponse()
                .setStatus(spec.getStatus())
                .setCode(spec.getCode())
                .setMessage(spec.getMessage())
                .setDetails(spec.getDetails())
                .setTraceId(metadata.traceId())
                .setTimestamp(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .setPath(req.path().rawPath())
                .setMethod(req.prologue().method().text());

        if (!Env.current().isProd()) {
            var list = Arrays.stream(ex.getStackTrace())
                    .map(StackTraceElement::toString)
                    .toList();
            response.setStacktrace(list);
        }
        return response;
    }
}

package cool.houge.mahu.web;

import static io.helidon.http.HeaderNames.USER_AGENT;
import static io.helidon.http.HeaderNames.X_FORWARDED_FOR;

import com.google.common.base.Splitter;
import cool.houge.mahu.TraceIdGenerator;
import cool.houge.mahu.common.Metadata;
import io.helidon.http.HeaderName;
import io.helidon.http.HeaderNames;
import io.helidon.webserver.http.ServerRequest;
import java.util.Optional;

/// Web 元数据.
///
/// @author ZY (kzou227@qq.com)
public class WebMetadata implements Metadata {

    private static final Splitter COMMA_SPLITTER = Splitter.on(',').omitEmptyStrings();
    private static final HeaderName X_REQUEST_ID = HeaderNames.create("x-request-id");

    private final ServerRequest request;
    private final String traceId;

    public WebMetadata(ServerRequest request) {
        this.request = request;
        this.traceId = request.headers().first(X_REQUEST_ID).orElseGet(TraceIdGenerator::generate);
    }

    @Override
    public String clientAddr() {
        return request.headers()
                .first(X_FORWARDED_FOR)
                .flatMap(s -> {
                    var ipList = COMMA_SPLITTER.splitToList(s);
                    if (ipList.isEmpty()) {
                        return Optional.empty();
                    }
                    return Optional.of(ipList.getFirst());
                })
                .orElseGet(() -> request.remotePeer().host());
    }

    @Override
    public String userAgent() {
        return request.headers().first(USER_AGENT).orElse("UNKNOWN");
    }

    @Override
    public String traceId() {
        return traceId;
    }
}

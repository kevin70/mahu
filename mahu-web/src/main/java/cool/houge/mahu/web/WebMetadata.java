package cool.houge.mahu.web;

import static io.helidon.http.HeaderNames.USER_AGENT;
import static io.helidon.http.HeaderNames.X_FORWARDED_FOR;

import com.google.common.base.Splitter;
import cool.houge.mahu.util.Metadata;
import io.helidon.webserver.http.ServerRequest;
import java.util.Optional;

/// Web 元数据.
///
/// @author ZY (kzou227@qq.com)
public class WebMetadata implements Metadata {

    private static final Splitter COMMA_SPLITTER = Splitter.on(',').omitEmptyStrings();
    private static final int MAX_USER_AGENT_LENGTH = 512;

    private final ServerRequest request;
    private final String userAgent;
    private final String traceId;
    private final int apiVersion;

    public WebMetadata(ServerRequest request, String traceId, int apiVersion) {
        this.request = request;
        this.userAgent = resolveUserAgent(request);
        this.traceId = traceId;
        this.apiVersion = apiVersion;
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
        return userAgent;
    }

    @Override
    public String traceId() {
        return traceId;
    }

    @Override
    public int apiVersion() {
        return apiVersion;
    }

    private static String resolveUserAgent(ServerRequest request) {
        var userAgent = request.headers().first(USER_AGENT).orElse("UNKNOWN");
        return userAgent.length() <= MAX_USER_AGENT_LENGTH
                ? userAgent
                : userAgent.substring(0, MAX_USER_AGENT_LENGTH);
    }
}

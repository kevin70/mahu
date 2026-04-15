package cool.houge.mahu.web.filter;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.G;
import cool.houge.mahu.util.Metadata;
import cool.houge.mahu.web.WebMetadata;
import io.helidon.http.HeaderName;
import io.helidon.http.HeaderNames;
import io.helidon.logging.common.HelidonMdc;
import io.helidon.webserver.http.Filter;
import io.helidon.webserver.http.FilterChain;
import io.helidon.webserver.http.RoutingRequest;
import io.helidon.webserver.http.RoutingResponse;
import io.helidon.webserver.http.ServerRequest;
import java.util.Objects;
import java.util.Set;

/// 应用级请求上下文 Filter。
///
/// - 统一生成/传递 traceId，并写入响应 Header
/// - 将 Metadata（WebMetadata）注入到请求上下文
public class AppRequestContextFilter implements Filter {

    private static final HeaderName X_REQUEST_ID = HeaderNames.create("X-Request-Id");
    private static final HeaderName X_API_VERSION = HeaderNames.create("X-API-Version");
    private static final int DEFAULT_API_VERSION = 1;
    private final Set<Integer> supportedApiVersions;

    public AppRequestContextFilter(Set<Integer> supportedApiVersions) {
        this.supportedApiVersions = Set.copyOf(Objects.requireNonNull(supportedApiVersions, "supportedApiVersions"));
    }

    @Override
    public void filter(FilterChain chain, RoutingRequest request, RoutingResponse response) {
        var traceId = resolveTraceId(request);
        var apiVersion = resolveApiVersion(request);
        try {
            beforeRequest(traceId, apiVersion, request, response);
            chain.proceed();
        } finally {
            afterRequest();
        }
    }

    private void beforeRequest(String traceId, int apiVersion, RoutingRequest request, RoutingResponse response) {
        HelidonMdc.set(G.MDC_TRACE_ID, traceId);
        response.beforeSend(() -> response.header(X_REQUEST_ID, traceId));

        var ctx = request.context();
        ctx.supply(Metadata.class, () -> new WebMetadata(request, traceId, apiVersion));
    }

    private void afterRequest() {
        HelidonMdc.remove(G.MDC_TRACE_ID);
    }

    String resolveTraceId(ServerRequest request) {
        return request.headers().first(X_REQUEST_ID).orElseGet(G::traceId);
    }

    int resolveApiVersion(ServerRequest request) {
        var rawValue = request.headers().first(X_API_VERSION).map(String::trim).orElse("");
        if (rawValue.isEmpty()) {
            return DEFAULT_API_VERSION;
        }
        var version = parseApiVersion(rawValue);
        validateSupportedApiVersion(version);
        return version;
    }

    private int parseApiVersion(String rawValue) {
        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException _) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "X-API-Version 必须为整数");
        }
    }

    private void validateSupportedApiVersion(int version) {
        if (!supportedApiVersions.contains(version)) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "不支持的 X-API-Version: " + version);
        }
    }
}

package cool.houge.mahu.admin.web.filter;

import static io.helidon.http.HeaderNames.AUTHORIZATION;

import cool.houge.mahu.G;
import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.admin.security.TokenVerifier;
import cool.houge.mahu.util.Metadata;
import cool.houge.mahu.web.WebMetadata;
import io.helidon.http.HeaderName;
import io.helidon.http.HeaderNames;
import io.helidon.http.UnauthorizedException;
import io.helidon.logging.common.HelidonMdc;
import io.helidon.security.jwt.JwtException;
import io.helidon.service.registry.Service;
import io.helidon.webserver.http.Filter;
import io.helidon.webserver.http.FilterChain;
import io.helidon.webserver.http.RoutingRequest;
import io.helidon.webserver.http.RoutingResponse;
import io.helidon.webserver.http.ServerRequest;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;

/// 应用级请求上下文 Filter。
///
/// - 统一生成/传递 traceId，并写入响应 Header
/// - 将 Metadata（WebMetadata）和 AuthContext 注入到请求上下文
@Service.Singleton
@AllArgsConstructor
public class AppRequestContextFilter implements Filter {

    private static final HeaderName X_REQUEST_ID = HeaderNames.create("x-request-id");
    private static final String SCHEME_PREFIX = "Bearer ";

    private final TokenVerifier tokenVerifier;

    @Override
    public void filter(FilterChain chain, RoutingRequest request, RoutingResponse response) {
        var traceId = resolveTraceId(request);
        try {
            beforeRequest(traceId, request, response);
            chain.proceed();
        } finally {
            afterRequest();
        }
    }

    private void beforeRequest(String traceId, RoutingRequest request, RoutingResponse response) {
        HelidonMdc.set(G.MDC_TRACE_ID, traceId);
        response.beforeSend(() -> response.header(X_REQUEST_ID, traceId));

        var ctx = request.context();
        ctx.supply(Metadata.class, () -> new WebMetadata(request, traceId));
        ctx.supply(AuthContext.class, () -> resolveAuthContext(request));
    }

    private void afterRequest() {
        HelidonMdc.remove(G.MDC_TRACE_ID);
    }

    AuthContext resolveAuthContext(RoutingRequest request) {
        return resolveToken(request).map(this::verifyToken).orElse(AuthContext.ANONYMOUS);
    }

    private AuthContext verifyToken(String token) {
        try {
            return tokenVerifier.verify(token);
        } catch (JwtException e) {
            throw new UnauthorizedException("认证失败", e);
        }
    }

    Optional<String> resolveToken(RoutingRequest request) {
        Supplier<Optional<String>> headerTokenSupplier = () -> request.headers()
                .first(AUTHORIZATION)
                .filter(s -> s.length() > SCHEME_PREFIX.length())
                .map(s -> s.substring(SCHEME_PREFIX.length()));

        return request.query()
                .first("access_token")
                .or(headerTokenSupplier)
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty));
    }

    String resolveTraceId(ServerRequest request) {
        return request.headers().first(X_REQUEST_ID).orElseGet(G::traceId);
    }
}

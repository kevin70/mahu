package cool.houge.mahu.admin;

import static io.helidon.http.HeaderNames.AUTHORIZATION;

import cool.houge.mahu.G;
import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.admin.security.TokenVerifier;
import cool.houge.mahu.util.Metadata;
import cool.houge.mahu.web.WebMetadata;
import cool.houge.mahu.web.problem.RestErrorHandler;
import cool.houge.mahu.web.security.SimpleHttpSecurity;
import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.http.ForbiddenException;
import io.helidon.http.HeaderName;
import io.helidon.http.HeaderNames;
import io.helidon.http.UnauthorizedException;
import io.helidon.logging.common.HelidonMdc;
import io.helidon.security.jwt.JwtException;
import io.helidon.service.registry.Service;
import io.helidon.webserver.http.Filter;
import io.helidon.webserver.http.FilterChain;
import io.helidon.webserver.http.HttpFeature;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.http.HttpSecurity;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.RoutingRequest;
import io.helidon.webserver.http.RoutingResponse;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;

/// 应用 HTTP 功能注册
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@AllArgsConstructor
@Weight(Weighted.DEFAULT_WEIGHT + 5)
public class AppHttpFeature implements HttpFeature, Filter {

    private static final HeaderName X_REQUEST_ID = HeaderNames.create("x-request-id");
    private static final String SCHEME_PREFIX = "Bearer ";

    private final List<HttpService> httpServices;
    private final List<Filter> filters;
    private final TokenVerifier tokenVerifier;

    @Override
    public void setup(HttpRouting.Builder routing) {
        routing.error(Throwable.class, new RestErrorHandler())
                .addFilter(this)
                .security(new SimpleHttpSecurity());
        for (Filter filter : filters) {
            routing.addFilter(filter);
        }

        // 注册 HTTP 服务
        for (HttpService httpService : httpServices) {
            routing.register(httpService);
        }
    }

    @Override
    public void filter(FilterChain chain, RoutingRequest request, RoutingResponse response) {
        var traceId = traceId(request);
        try {
            HelidonMdc.set(G.MDC_TRACE_ID, traceId);
            response.beforeSend(() -> response.header(X_REQUEST_ID, traceId));

            var ctx = request.context();
            ctx.supply(Metadata.class, () -> new WebMetadata(request, traceId));
            ctx.supply(AuthContext.class, () -> authContext(request));
            chain.proceed();
        } finally {
            // 清理追踪 ID
            HelidonMdc.remove(G.MDC_TRACE_ID);
        }
    }

    AuthContext authContext(RoutingRequest request) {
        var token = token(request);
        if (token.isEmpty()) {
            return AuthContext.ANONYMOUS;
        }

        try {
            // 校验访问令牌
            return tokenVerifier.verify(token.get());
        } catch (JwtException e) {
            throw new UnauthorizedException("认证失败", e);
        }
    }

    Optional<String> token(RoutingRequest request) {
        Supplier<Optional<String>> headerGet = () -> request.headers()
                .first(AUTHORIZATION)
                .filter(s -> s.length() > SCHEME_PREFIX.length())
                .map(s -> s.substring(SCHEME_PREFIX.length()));
        return request.query()
                .first("access_token")
                .or(headerGet)
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty));
    }

    String traceId(ServerRequest request) {
        return request.headers().first(X_REQUEST_ID).orElseGet(G::traceId);
    }
}

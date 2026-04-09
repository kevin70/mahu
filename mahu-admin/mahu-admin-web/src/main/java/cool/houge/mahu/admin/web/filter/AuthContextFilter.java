package cool.houge.mahu.admin.web.filter;

import static io.helidon.http.HeaderNames.AUTHORIZATION;

import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.admin.security.TokenVerifier;
import io.helidon.http.UnauthorizedException;
import io.helidon.security.jwt.JwtException;
import io.helidon.service.registry.Service;
import io.helidon.webserver.http.Filter;
import io.helidon.webserver.http.FilterChain;
import io.helidon.webserver.http.RoutingRequest;
import io.helidon.webserver.http.RoutingResponse;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;

/// 认证上下文注入 Filter。
///
/// 负责解析访问令牌并将 [AuthContext] 注入请求上下文。
@Service.Singleton
@AllArgsConstructor
public class AuthContextFilter implements Filter {

    private static final String ACCESS_TOKEN_PARAM = "access_token";
    private static final String SCHEME_PREFIX = "Bearer ";

    private final TokenVerifier tokenVerifier;

    @Override
    public void filter(FilterChain chain, RoutingRequest request, RoutingResponse response) {
        request.context().supply(AuthContext.class, () -> resolveAuthContext(request));
        chain.proceed();
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
        return request.query()
                .first(ACCESS_TOKEN_PARAM)
                .or(() -> resolveBearerToken(request))
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty));
    }

    private Optional<String> resolveBearerToken(RoutingRequest request) {
        return request.headers().first(AUTHORIZATION).flatMap(this::extractBearerToken);
    }

    private Optional<String> extractBearerToken(String authorization) {
        if (!authorization.startsWith(SCHEME_PREFIX)) {
            return Optional.empty();
        }
        return Optional.of(authorization.substring(SCHEME_PREFIX.length()));
    }
}

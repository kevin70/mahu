package cool.houge.mahu.admin.web.filter;

import static io.helidon.http.HeaderNames.AUTHORIZATION;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.admin.security.TokenVerifier;
import cool.houge.mahu.util.Metadata;
import io.helidon.http.UnauthorizedException;
import io.helidon.security.jwt.JwtException;
import io.helidon.service.registry.Service;
import io.helidon.webserver.http.Filter;
import io.helidon.webserver.http.FilterChain;
import io.helidon.webserver.http.RoutingRequest;
import io.helidon.webserver.http.RoutingResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 认证上下文注入 Filter。
///
/// 负责解析访问令牌并将 [AuthContext] 注入请求上下文。
@Service.Singleton
public class AuthContextFilter implements Filter {

    private static final String ACCESS_TOKEN_PARAM = "access_token";
    private static final String SCHEME_PREFIX = "Bearer ";
    private static final String TOKEN_SOURCE_QUERY = "query";
    private static final String TOKEN_SOURCE_HEADER = "header";
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(30);
    private static final Logger log = LogManager.getLogger(AuthContextFilter.class);

    private final TokenVerifier tokenVerifier;
    private final IpAuthFailureTracker failureTracker;

    public AuthContextFilter(TokenVerifier tokenVerifier, IpAuthFailureTracker failureTracker) {
        this.tokenVerifier = tokenVerifier;
        this.failureTracker = failureTracker;
    }

    @Override
    public void filter(FilterChain chain, RoutingRequest request, RoutingResponse response) {
        request.context().supply(AuthContext.class, () -> resolveAuthContext(request));
        chain.proceed();
    }

    AuthContext resolveAuthContext(RoutingRequest request) {
        var clientIp = resolveClientIp(request);
        var blockedUntil = failureTracker.blockedUntil(clientIp, now());
        if (blockedUntil.isPresent()) {
            log.warn(
                    "auth_ip_blocked ip={} blockedUntil={} path={}",
                    clientIp,
                    blockedUntil.get(),
                    request.path().rawPath());
            throw new BizCodeException(BizCodes.RESOURCE_EXHAUSTED, "失败次数过多，请 30 分钟后再试");
        }
        return resolveToken(request)
                .map(token -> verifyToken(token.value(), token.source(), clientIp))
                .orElse(AuthContext.ANONYMOUS);
    }

    private AuthContext verifyToken(String token, String tokenSource, String clientIp) {
        try {
            return tokenVerifier.verify(token);
        } catch (JwtException e) {
            recordVerifyFailure(clientIp, tokenSource);
            throw new UnauthorizedException("认证失败", e);
        }
    }

    Optional<ResolvedToken> resolveToken(RoutingRequest request) {
        var queryToken =
                request.query().first(ACCESS_TOKEN_PARAM).map(String::trim).filter(Predicate.not(String::isEmpty));
        if (queryToken.isPresent()) {
            return queryToken.map(v -> new ResolvedToken(v, TOKEN_SOURCE_QUERY));
        }
        return resolveBearerToken(request).map(v -> new ResolvedToken(v, TOKEN_SOURCE_HEADER));
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

    private String resolveClientIp(RoutingRequest request) {
        return request.context()
                .get(Metadata.class)
                .map(Metadata::clientAddr)
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty))
                .orElse("UNKNOWN");
    }

    private void recordVerifyFailure(String clientIp, String tokenSource) {
        var failedAttempts = failureTracker.incrementFailure(clientIp);
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            var blockedUntil = now().plus(BLOCK_DURATION);
            failureTracker.block(clientIp, blockedUntil);
            log.warn(
                    "auth_verify_failed_blocked ip={} failCount={} blockedUntil={} tokenSource={}",
                    clientIp,
                    failedAttempts,
                    blockedUntil,
                    tokenSource);
            return;
        }
        log.warn("auth_verify_failed ip={} failCount={} tokenSource={}", clientIp, failedAttempts, tokenSource);
    }

    private Instant now() {
        return Instant.now();
    }

    private record ResolvedToken(String value, String source) {}
}

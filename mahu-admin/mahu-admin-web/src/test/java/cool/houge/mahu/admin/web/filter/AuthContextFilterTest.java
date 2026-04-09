package cool.houge.mahu.admin.web.filter;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.admin.security.TokenVerifier;
import cool.houge.mahu.util.Metadata;
import io.helidon.common.mapper.OptionalValue;
import io.helidon.http.UnauthorizedException;
import io.helidon.security.jwt.JwtException;
import io.helidon.webserver.http.RoutingRequest;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;

class AuthContextFilterTest {

    private final TokenVerifier tokenVerifier = mock(TokenVerifier.class);
    private final IpAuthFailureTracker failureTracker = new CaffeineIpAuthFailureTracker();
    private final AuthContextFilter sut = new AuthContextFilter(tokenVerifier, failureTracker);

    @Test
    void resolveAuthContext_未携带令牌_返回匿名上下文() {
        var request = mock(RoutingRequest.class, Answers.RETURNS_DEEP_STUBS);
        mockClientIp(request);
        mockEmptyAccessTokenQuery(request);
        when(request.headers().first(io.helidon.http.HeaderNames.AUTHORIZATION))
                .thenReturn(Optional.empty());

        var authContext = sut.resolveAuthContext(request);

        assertSame(AuthContext.ANONYMOUS, authContext);
    }

    @Test
    void resolveAuthContext_携带Bearer令牌_返回校验结果() {
        var request = mock(RoutingRequest.class, Answers.RETURNS_DEEP_STUBS);
        var expected = mock(AuthContext.class);
        mockClientIp(request);
        mockEmptyAccessTokenQuery(request);
        when(request.headers().first(io.helidon.http.HeaderNames.AUTHORIZATION))
                .thenReturn(Optional.of("Bearer token-123"));
        when(tokenVerifier.verify("token-123")).thenReturn(expected);

        var authContext = sut.resolveAuthContext(request);

        assertSame(expected, authContext);
    }

    @Test
    void resolveAuthContext_令牌校验失败_抛出未认证异常() {
        var request = mock(RoutingRequest.class, Answers.RETURNS_DEEP_STUBS);
        mockClientIp(request);
        mockEmptyAccessTokenQuery(request);
        when(request.headers().first(io.helidon.http.HeaderNames.AUTHORIZATION))
                .thenReturn(Optional.of("Bearer bad-token"));
        when(tokenVerifier.verify("bad-token")).thenThrow(new JwtException("bad"));

        assertThrows(UnauthorizedException.class, () -> sut.resolveAuthContext(request));
    }

    @Test
    void resolveAuthContext_授权头非Bearer_返回匿名上下文() {
        var request = mock(RoutingRequest.class, Answers.RETURNS_DEEP_STUBS);
        mockClientIp(request);
        mockEmptyAccessTokenQuery(request);
        when(request.headers().first(io.helidon.http.HeaderNames.AUTHORIZATION))
                .thenReturn(Optional.of("Basic dGVzdDp0ZXN0"));

        var authContext = sut.resolveAuthContext(request);

        assertSame(AuthContext.ANONYMOUS, authContext);
        verifyNoInteractions(tokenVerifier);
    }

    @Test
    void resolveAuthContext_失败5次后命中封禁_不再调用verify() {
        var request = mock(RoutingRequest.class, Answers.RETURNS_DEEP_STUBS);
        mockClientIp(request);
        mockEmptyAccessTokenQuery(request);
        when(request.headers().first(io.helidon.http.HeaderNames.AUTHORIZATION))
                .thenReturn(Optional.of("Bearer bad-token"));
        when(tokenVerifier.verify("bad-token")).thenThrow(new JwtException("bad"));

        for (int i = 0; i < 5; i++) {
            assertThrows(UnauthorizedException.class, () -> sut.resolveAuthContext(request));
        }

        // 封禁命中：返回资源耗尽错误码，不再触发 token 校验。
        var ex = assertThrows(BizCodeException.class, () -> sut.resolveAuthContext(request));
        org.junit.jupiter.api.Assertions.assertEquals(BizCodes.RESOURCE_EXHAUSTED, ex.getCode());
    }

    @Test
    void resolveAuthContext_封禁过期后可再次进入校验流程() {
        var request = mock(RoutingRequest.class, Answers.RETURNS_DEEP_STUBS);
        var expected = mock(AuthContext.class);
        mockClientIp(request);
        mockEmptyAccessTokenQuery(request);
        when(request.headers().first(io.helidon.http.HeaderNames.AUTHORIZATION))
                .thenReturn(Optional.of("Bearer token-123"));

        // 直接注入一个已过期封禁记录，验证会自动失效并允许继续认证。
        failureTracker.block("10.0.0.1", Instant.now().minusSeconds(60));
        doReturn(expected).when(tokenVerifier).verify("token-123");
        var authContext = sut.resolveAuthContext(request);
        assertSame(expected, authContext);
    }

    @Test
    void resolveAuthContext_成功后清空失败状态() {
        var request = mock(RoutingRequest.class, Answers.RETURNS_DEEP_STUBS);
        var expected = mock(AuthContext.class);
        mockClientIp(request);
        mockEmptyAccessTokenQuery(request);
        when(request.headers().first(io.helidon.http.HeaderNames.AUTHORIZATION))
                .thenReturn(Optional.of("Bearer token-abc"));

        when(tokenVerifier.verify("token-abc"))
                .thenThrow(new JwtException("bad-1"))
                .thenThrow(new JwtException("bad-2"))
                .thenReturn(expected)
                .thenThrow(new JwtException("bad-3"))
                .thenThrow(new JwtException("bad-4"))
                .thenThrow(new JwtException("bad-5"))
                .thenThrow(new JwtException("bad-6"));

        assertThrows(UnauthorizedException.class, () -> sut.resolveAuthContext(request));
        assertThrows(UnauthorizedException.class, () -> sut.resolveAuthContext(request));
        var authContext = sut.resolveAuthContext(request);
        assertSame(expected, authContext);

        // 若成功清空失败状态，则再失败 3 次不会触发封禁。
        assertThrows(UnauthorizedException.class, () -> sut.resolveAuthContext(request));
        assertThrows(UnauthorizedException.class, () -> sut.resolveAuthContext(request));
        assertThrows(UnauthorizedException.class, () -> sut.resolveAuthContext(request));
    }

    @SuppressWarnings("unchecked")
    private static void mockEmptyAccessTokenQuery(RoutingRequest request) {
        var accessToken = mock(OptionalValue.class);
        when(request.query().first("access_token")).thenReturn(accessToken);
        when(accessToken.or(any())).thenAnswer(invocation -> {
            Supplier<Optional<String>> supplier = invocation.getArgument(0);
            return supplier.get();
        });
    }

    private static void mockClientIp(RoutingRequest request) {
        var metadata = mock(Metadata.class);
        when(metadata.clientAddr()).thenReturn("10.0.0.1");
        when(request.context().get(Metadata.class)).thenReturn(Optional.of(metadata));
    }
}

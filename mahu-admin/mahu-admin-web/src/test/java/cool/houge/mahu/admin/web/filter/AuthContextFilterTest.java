package cool.houge.mahu.admin.web.filter;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.admin.security.TokenVerifier;
import io.helidon.common.mapper.OptionalValue;
import io.helidon.http.UnauthorizedException;
import io.helidon.security.jwt.JwtException;
import io.helidon.webserver.http.RoutingRequest;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;

class AuthContextFilterTest {

    private final TokenVerifier tokenVerifier = mock(TokenVerifier.class);
    private final AuthContextFilter sut = new AuthContextFilter(tokenVerifier);

    @Test
    void resolveAuthContext_未携带令牌_返回匿名上下文() {
        var request = mock(RoutingRequest.class, Answers.RETURNS_DEEP_STUBS);
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
        mockEmptyAccessTokenQuery(request);
        when(request.headers().first(io.helidon.http.HeaderNames.AUTHORIZATION))
                .thenReturn(Optional.of("Bearer bad-token"));
        when(tokenVerifier.verify("bad-token")).thenThrow(new JwtException("bad"));

        assertThrows(UnauthorizedException.class, () -> sut.resolveAuthContext(request));
    }

    @Test
    void resolveAuthContext_授权头非Bearer_返回匿名上下文() {
        var request = mock(RoutingRequest.class, Answers.RETURNS_DEEP_STUBS);
        mockEmptyAccessTokenQuery(request);
        when(request.headers().first(io.helidon.http.HeaderNames.AUTHORIZATION))
                .thenReturn(Optional.of("Basic dGVzdDp0ZXN0"));

        var authContext = sut.resolveAuthContext(request);

        assertSame(AuthContext.ANONYMOUS, authContext);
        verifyNoInteractions(tokenVerifier);
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
}

package cool.houge.mahu.web.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import io.helidon.http.HeaderName;
import io.helidon.http.HeaderNames;
import io.helidon.webserver.http.ServerRequest;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;

class AppRequestContextFilterTest {

    private static final HeaderName X_API_VERSION = HeaderNames.create("X-API-Version");

    private final AppRequestContextFilter sut = new AppRequestContextFilter(Set.of(1));

    @Test
    void resolveApiVersion_默认版本_返回1() {
        var request = mock(ServerRequest.class, Answers.RETURNS_DEEP_STUBS);
        when(request.headers().first(X_API_VERSION)).thenReturn(java.util.Optional.empty());

        var version = sut.resolveApiVersion(request);

        assertEquals(1, version);
    }

    @Test
    void resolveApiVersion_支持版本_返回对应值() {
        var request = mock(ServerRequest.class, Answers.RETURNS_DEEP_STUBS);
        when(request.headers().first(X_API_VERSION)).thenReturn(java.util.Optional.of("1"));

        var version = sut.resolveApiVersion(request);

        assertEquals(1, version);
    }

    @Test
    void resolveApiVersion_非整数_抛出非法参数异常() {
        var request = mock(ServerRequest.class, Answers.RETURNS_DEEP_STUBS);
        when(request.headers().first(X_API_VERSION)).thenReturn(java.util.Optional.of("v2"));

        var ex = assertThrows(BizCodeException.class, () -> sut.resolveApiVersion(request));

        assertEquals(BizCodes.INVALID_ARGUMENT, ex.getCode());
    }

    @Test
    void resolveApiVersion_不支持版本_抛出非法参数异常() {
        var request = mock(ServerRequest.class, Answers.RETURNS_DEEP_STUBS);
        when(request.headers().first(X_API_VERSION)).thenReturn(java.util.Optional.of("2"));

        var ex = assertThrows(BizCodeException.class, () -> sut.resolveApiVersion(request));

        assertEquals(BizCodes.INVALID_ARGUMENT, ex.getCode());
    }
}

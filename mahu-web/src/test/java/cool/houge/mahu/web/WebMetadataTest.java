package cool.houge.mahu.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.helidon.http.HeaderNames;
import io.helidon.webserver.http.ServerRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;

class WebMetadataTest {

    @Test
    void userAgent_缺少请求头时返回UNKNOWN() {
        var request = mock(ServerRequest.class, Answers.RETURNS_DEEP_STUBS);
        when(request.headers().first(HeaderNames.USER_AGENT)).thenReturn(java.util.Optional.empty());

        var metadata = new WebMetadata(request, "trace-1", 1);

        assertEquals("UNKNOWN", metadata.userAgent());
    }

    @Test
    void userAgent_超长时截断为512个字符() {
        var request = mock(ServerRequest.class, Answers.RETURNS_DEEP_STUBS);
        var longUserAgent = "a".repeat(600);
        when(request.headers().first(HeaderNames.USER_AGENT)).thenReturn(java.util.Optional.of(longUserAgent));

        var metadata = new WebMetadata(request, "trace-1", 1);

        assertEquals(512, metadata.userAgent().length());
        assertEquals(longUserAgent.substring(0, 512), metadata.userAgent());
    }

    @Test
    void userAgent_重复读取时复用构造期缓存() {
        var request = mock(ServerRequest.class, Answers.RETURNS_DEEP_STUBS);
        when(request.headers().first(HeaderNames.USER_AGENT)).thenReturn(java.util.Optional.of("ua"));

        var metadata = new WebMetadata(request, "trace-1", 1);

        assertEquals("ua", metadata.userAgent());
        assertEquals("ua", metadata.userAgent());
        verify(request.headers(), times(1)).first(HeaderNames.USER_AGENT);
    }
}

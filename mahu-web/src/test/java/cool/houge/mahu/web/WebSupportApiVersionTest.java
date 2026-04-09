package cool.houge.mahu.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cool.houge.mahu.util.Metadata;
import io.helidon.webserver.http.ServerRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;

class WebSupportApiVersionTest {

    private final WebSupport sut = new WebSupport() {};

    @Test
    void apiVersion_上下文存在metadata时返回metadata版本() {
        var request = mock(ServerRequest.class, Answers.RETURNS_DEEP_STUBS);
        var metadata = mock(Metadata.class);
        when(metadata.apiVersion()).thenReturn(2);
        when(request.context().get(Metadata.class)).thenReturn(java.util.Optional.of(metadata));

        var version = sut.apiVersion(request);

        assertEquals(2, version);
    }

    @Test
    void apiVersion_上下文缺少metadata时回退默认版本1() {
        var request = mock(ServerRequest.class, Answers.RETURNS_DEEP_STUBS);
        when(request.context().get(Metadata.class)).thenReturn(java.util.Optional.empty());

        var version = sut.apiVersion(request);

        assertEquals(1, version);
    }
}

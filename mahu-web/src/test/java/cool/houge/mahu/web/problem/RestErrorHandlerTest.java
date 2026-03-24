package cool.houge.mahu.web.problem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Answers;

class RestErrorHandlerTest {

    @Test
    void shouldHandleExactExceptionType() {
        var spec = new ProblemSpec().setStatus(400).setCode(140003).setMessage("invalid");
        var handler = new TrackingHandler(IllegalArgumentException.class, spec);

        var factory = mock(ProblemResponseFactory.class);
        var response = new ProblemResponse().setStatus(400).setCode(140003).setMessage("invalid");
        when(factory.from(any(), any(), eq(spec))).thenReturn(response);

        var sut = new RestErrorHandler(List.of(handler), factory);
        var req = mockRequest();
        var res = mockResponse();

        var ex = new IllegalArgumentException("bad");
        sut.handle(req, res, ex);

        assertEquals(ex, handler.lastHandled);
        verify(factory).from(req, ex, spec);
        verify(res).status(400);
    }

    @Test
    void shouldHandleCauseWhenTopLevelNotMatched() {
        var spec = new ProblemSpec().setStatus(400).setCode(140003).setMessage("invalid");
        var handler = new TrackingHandler(IllegalArgumentException.class, spec);

        var factory = mock(ProblemResponseFactory.class);
        var response = new ProblemResponse().setStatus(400).setCode(140003).setMessage("invalid");
        when(factory.from(any(), any(), eq(spec))).thenReturn(response);

        var sut = new RestErrorHandler(List.of(handler), factory);
        var req = mockRequest();
        var res = mockResponse();

        var cause = new IllegalArgumentException("root-cause");
        var ex = new RuntimeException("wrapper", cause);
        sut.handle(req, res, ex);

        assertEquals(cause, handler.lastHandled);
        verify(factory).from(req, ex, spec);
    }

    @Test
    void shouldFallbackToInternalWhenNoHandlerMatched() {
        var handler = new TrackingHandler(IllegalArgumentException.class, new ProblemSpec());

        var factory = mock(ProblemResponseFactory.class);
        var response = new ProblemResponse().setStatus(500).setCode(150013).setMessage("internal");
        when(factory.from(any(), any(), any())).thenReturn(response);

        var sut = new RestErrorHandler(List.of(handler), factory);
        var req = mockRequest();
        var res = mockResponse();

        var ex = new RuntimeException("oops");
        sut.handle(req, res, ex);

        assertEquals(null, handler.lastHandled);

        ArgumentCaptor<ProblemSpec> specCaptor = ArgumentCaptor.forClass(ProblemSpec.class);
        verify(factory).from(eq(req), eq(ex), specCaptor.capture());
        assertEquals(500, specCaptor.getValue().getStatus());
        assertEquals(150013, specCaptor.getValue().getCode());
        assertEquals("oops", specCaptor.getValue().getMessage());

        @SuppressWarnings("unchecked")
        var payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(res).send(payloadCaptor.capture());
        assertEquals(response, payloadCaptor.getValue().get("error"));
    }

    private static ServerRequest mockRequest() {
        var req = mock(ServerRequest.class, Answers.RETURNS_DEEP_STUBS);
        when(req.path().rawPath()).thenReturn("/t");
        when(req.prologue().method().text()).thenReturn("GET");
        return req;
    }

    private static ServerResponse mockResponse() {
        var res = mock(ServerResponse.class);
        when(res.status(any(Integer.class))).thenReturn(res);
        return res;
    }

    private static final class TrackingHandler implements ProblemHandler {
        private final Class<? extends Throwable> type;
        private final ProblemSpec spec;
        private Throwable lastHandled;

        private TrackingHandler(Class<? extends Throwable> type, ProblemSpec spec) {
            this.type = type;
            this.spec = spec;
        }

        @Override
        public ProblemSpec handle(Throwable ex) {
            this.lastHandled = ex;
            return spec;
        }

        @Override
        public Class<? extends Throwable> exceptionType() {
            return type;
        }
    }
}

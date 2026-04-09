package cool.houge.mahu.web.problem.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cool.houge.mahu.BizCodes;
import io.helidon.http.ForbiddenException;
import org.junit.jupiter.api.Test;

class ForbiddenExceptionHandlerTest {

    private final ForbiddenExceptionHandler handler = new ForbiddenExceptionHandler();

    @Test
    void handle_shouldMapToPermissionDenied() {
        var ex = new ForbiddenException("失败次数过多，请稍后再试");

        var spec = handler.handle(ex);

        assertEquals(403, spec.getStatus());
        assertEquals(BizCodes.PERMISSION_DENIED.code(), spec.getCode());
        assertEquals("失败次数过多，请稍后再试", spec.getMessage());
    }
}

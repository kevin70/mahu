package cool.houge.mahu.web.problem;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ProblemHandlerTest {

    @Test
    void shouldMatchWithAssignableExceptionType() {
        ProblemHandler handler = new TestHandler(RuntimeException.class);
        assertTrue(handler.exceptionType().isInstance(new IllegalArgumentException("x")));
    }

    @Test
    void shouldNotMatchWithUnrelatedExceptionType() {
        ProblemHandler handler = new TestHandler(IllegalArgumentException.class);
        assertFalse(handler.exceptionType().isInstance(new IllegalStateException("x")));
    }

    private record TestHandler(Class<? extends Throwable> exceptionType) implements ProblemHandler {
        @Override
        public ProblemSpec handle(Throwable ex) {
            return new ProblemSpec();
        }
    }
}

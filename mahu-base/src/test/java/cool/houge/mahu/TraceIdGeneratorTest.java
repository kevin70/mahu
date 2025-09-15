package cool.houge.mahu;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

///
/// @author ZY (kzou227@qq.com)
class TraceIdGeneratorTest {

    @Test
    void generate() {
        var a = TraceIdGenerator.generate();
        var b = TraceIdGenerator.generate();
        assertThat(a).isNotNull().hasSizeBetween(20, 22);
        assertThat(b).isNotNull().hasSizeBetween(20, 22);
      }
}

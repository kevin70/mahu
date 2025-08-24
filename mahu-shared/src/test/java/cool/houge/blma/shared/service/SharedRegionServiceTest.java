package cool.houge.blma.shared.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.helidon.testing.junit5.Testing;
import org.junit.jupiter.api.Test;

///
/// @author ZY (kzou227@qq.com)
@Testing.Test
class SharedRegionServiceTest {

    @Test
    void toFullRegion(SharedRegionService sharedRegionService) {
        var code = "420506104";
        var fullRegion = sharedRegionService.toFullRegion(code);
        assertThat(fullRegion).isEqualTo("湖北省宜昌市夷陵区太平溪镇");
    }
}

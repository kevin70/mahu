package cool.houge.mahu.repository;

import cool.houge.mahu.TestTransactionBase;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/// @author ZY (kzou227@qq.com)
class RegionRepositoryTest extends TestTransactionBase {

    @Inject
    RegionRepository regionRepository;

    /// 省级行政区
    @Test
    void findDepthBound_1() {
        var regions = regionRepository.findDepthBound(1);
        assertThat(regions).isNotNull().hasSize(31);
    }

    /// 省市行政区
    @Test
    void findDepthBound_2() {
        var regions = regionRepository.findDepthBound(2);
        assertThat(regions).isNotNull().hasSize(374);
    }

    /// 没有行政区
    @Test
    void findDepthBound_0() {
        var regions = regionRepository.findDepthBound(0);
        assertThat(regions).isEmpty();
    }
}

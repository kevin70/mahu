package cool.houge.mahu.common;

import io.helidon.common.Weighted;
import io.helidon.spi.HelidonShutdownHandler;

/// 带权重的停止处理器
///
/// @author ZY (kzou227@qq.com)
public class WeightedShutdownHandler implements HelidonShutdownHandler, Weighted {

    private final HelidonShutdownHandler delegate;
    private final double weight;

    public WeightedShutdownHandler(HelidonShutdownHandler delegate, double weight) {
        this.delegate = delegate;
        this.weight = weight;
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public double weight() {
        return weight;
    }

    @Override
    public String toString() {
        return delegate + ", " + weight;
    }
}

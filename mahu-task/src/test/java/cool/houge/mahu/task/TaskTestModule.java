package cool.houge.mahu.task;

import io.avaje.inject.spi.Builder;
import io.avaje.inject.test.TestModule;
import io.helidon.config.Config;

///
/// @author ZY (kzou227@qq.com)
public class TaskTestModule implements TestModule {

    @Override
    public Class<?>[] classes() {
        return new Class[] {Config.class};
    }

    @Override
    public void build(Builder builder) {
        builder.withBean(Config.class, Config.create());
    }
}

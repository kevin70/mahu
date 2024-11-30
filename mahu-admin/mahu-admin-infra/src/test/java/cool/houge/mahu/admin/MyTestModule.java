package cool.houge.mahu.admin;

import io.avaje.inject.spi.Builder;
import io.avaje.inject.test.TestModule;
import io.helidon.common.config.Config;
import io.helidon.common.config.GlobalConfig;

///
/// @author ZY (kzou227@qq.com)
public class MyTestModule implements TestModule {

    @Override
    public Class<?>[] classes() {
        return new Class[] {Config.class};
    }

    @Override
    public void build(Builder builder) {
        builder.withBean(Config.class, GlobalConfig.config());
    }
}

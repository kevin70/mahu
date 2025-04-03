package cool.houge.mahu.task;

import cool.houge.mahu.config.RefreshSwitchConfig;
import cool.houge.mahu.config.SwitchConfig;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.helidon.config.Config;

/// 配置对象定义工厂
///
/// @author ZY (kzou227@qq.com)
@Factory
public class ConfigBeanFactory {

    private final Config config;

    public ConfigBeanFactory(Config config) {
        this.config = config;
    }

    @Bean
    public SwitchConfig switchConfig() {
        return new RefreshSwitchConfig(config.get("switch"));
    }
}

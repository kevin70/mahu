package cool.houge.mahu;

import cool.houge.mahu.config.TokenConfig;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.helidon.common.config.Config;
import io.helidon.common.configurable.Resource;
import io.helidon.security.jwt.jwk.JwkKeys;

/// 系统配置对象工厂
///
/// @author ZY (kzou227@qq.com)
@Factory
public class ConfigBeanFactory {

    @Bean
    public JwkKeys jwkKeys(Config config) {
        return JwkKeys.builder()
                .resource(Resource.create(config.get("jwt.keys")))
                .build();
    }

    @Bean
    public TokenConfig tokenConfig(Config config) {
        return TokenConfig.create(config.get(TokenConfig.PREFIX));
    }
}

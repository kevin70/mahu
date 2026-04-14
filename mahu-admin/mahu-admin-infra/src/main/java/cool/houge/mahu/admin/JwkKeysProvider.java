package cool.houge.mahu.admin;

import cool.houge.mahu.config.ConfigPrefixes;
import io.helidon.common.configurable.Resource;
import io.helidon.config.Config;
import io.helidon.security.jwt.jwk.JwkKeys;
import io.helidon.service.registry.Service.Singleton;
import java.util.function.Supplier;

/// JWT Key 集合 Provider
///
/// @author ZY (kzou227@qq.com)
@Singleton
class JwkKeysProvider implements Supplier<JwkKeys> {

    private final JwkKeys v;

    JwkKeysProvider(Config root) {
        this.v = JwkKeys.builder()
                .resource(Resource.create(root.get(ConfigPrefixes.JWT_KEYS)))
                .build();
    }

    @Override
    public JwkKeys get() {
        return v;
    }
}

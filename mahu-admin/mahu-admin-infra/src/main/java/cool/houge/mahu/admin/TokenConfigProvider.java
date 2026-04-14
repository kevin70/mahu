package cool.houge.mahu.admin;

import cool.houge.mahu.config.ConfigPrefixes;
import cool.houge.mahu.config.TokenConfig;
import io.helidon.config.Config;
import io.helidon.service.registry.Service.Singleton;
import java.util.function.Supplier;

/// 令牌配置 Provider
///
/// @author ZY (kzou227@qq.com)
@Singleton
class TokenConfigProvider implements Supplier<TokenConfig> {

    private final TokenConfig v;

    TokenConfigProvider(Config root) {
        this.v = TokenConfig.create(root.get(ConfigPrefixes.TOKEN));
    }

    @Override
    public TokenConfig get() {
        return v;
    }
}

package cool.houge.mahu.config;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

import java.time.Duration;

import static cool.houge.mahu.config.TokenConfigBlueprint.PREFIX;

/// 访问令牌配置蓝图
///
/// @author ZY (kzou227@qq.com)
@Prototype.Blueprint(builderPublic = false, createEmptyPublic = false)
@Prototype.Configured(PREFIX)
interface TokenConfigBlueprint {

    /**
     * 默认前缀.
     */
    String PREFIX = "token";

    @Option.Configured
    Duration accessExpires();

    @Option.Configured
    Duration refreshExpires();
}

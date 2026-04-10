package cool.houge.mahu.config;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;
import java.time.Duration;

/// 令牌配置蓝图
///
/// 定义了应用中令牌（Token）相关的配置参数，包括访问令牌和刷新令牌的过期时间。
/// 对应配置前缀：`token.*`
///
/// 示例配置（application.yaml）：
/// ```yaml
/// token:
///   access-expires: 1h
///   refresh-expires: 7d
/// ```
@Prototype.Blueprint
@Prototype.Configured(ConfigPrefixes.TOKEN)
interface TokenConfigBlueprint {
    /// 访问令牌过期时间
    @Option.Configured
    Duration accessExpires();

    /// 刷新令牌过期时间
    @Option.Configured
    Duration refreshExpires();
}

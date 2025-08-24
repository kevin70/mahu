package cool.houge.mahu.config;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;
import java.time.Duration;

/// 访问令牌配置蓝图
///
/// @author ZY (kzou227@qq.com)
@Prototype.Blueprint
@Prototype.Configured(ConfigKeys.TOKEN)
interface TokenConfigBlueprint {

    /// 访问令牌过期时间
    @Option.Configured
    Duration accessExpires();

    /// 刷新令牌过期时间
    @Option.Configured
    Duration refreshExpires();
}

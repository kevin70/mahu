package cool.houge.mahu.config;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

import static cool.houge.mahu.config.InfoConfigBlueprint.PREFIX;

/// 应用信息配置
///
/// @author ZY (kzou227@qq.com)
@Prototype.Blueprint(builderPublic = false, createEmptyPublic = false)
@Prototype.Configured(PREFIX)
public interface InfoConfigBlueprint {

    /// 默认前缀
    String PREFIX = "info";

    /// 应用名称
    @Option.Configured
    String name();

    /// 应用版本
    @Option.Configured
    String version();
}

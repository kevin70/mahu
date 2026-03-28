package cool.houge.mahu.config;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Option.AllowedValue;
import io.helidon.builder.api.Prototype;

/// 应用信息配置蓝图
///
/// 定义了应用的基础信息配置，包括应用名称和运行环境。
/// 对应配置前缀：`info.*`
///
/// 示例配置（application.yaml）：
/// ```yaml
/// info:
///   name: "Mahu Backend API"
///   env: "dev"
/// ```
@Prototype.Blueprint
@Prototype.Configured(ConfigPrefixes.INFO)
interface InfoConfigBlueprint {

    /// 应用名称（必需）
    @Option.Required
    @Option.Configured
    String name();

    /// 运行环境（必需）
    /// 定义了应用当前运行的环境，影响日志级别、缓存策略等。
    @Option.Required
    @Option.Configured
    @AllowedValue(value = "dev", description = "开发环境，开发者本地或共享编码调试环境")
    @AllowedValue(value = "sit", description = "系统集成测试环境，验证多个模块/服务间的交互")
    @AllowedValue(value = "uat", description = "用户验收测试环境，业务方验证功能是否符合需求")
    @AllowedValue(value = "stg", description = "预发布环境，功能与[PROD]一致，用于最终验证")
    @AllowedValue(value = "prod", description = "生产环境，面向真实用户的线上环境")
    String env();
}

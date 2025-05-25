package cool.houge.mahu.config;

import static cool.houge.mahu.config.InfoConfigBlueprint.PREFIX;

import cool.houge.mahu.config.annotation.Refreshable;
import io.helidon.builder.api.Description;
import io.helidon.builder.api.Option;
import io.helidon.builder.api.Option.AllowedValue;
import io.helidon.builder.api.Prototype;

/// 应用信息配置
///
/// @author ZY (kzou227@qq.com)
@Refreshable
@Prototype.Blueprint
@Prototype.Configured(PREFIX)
interface InfoConfigBlueprint {

    /// 默认前缀
    String PREFIX = "info";

    /// 应用名称
    @Description("应用名称")
    @Option.Configured
    String name();

    /// 应用版本
    @Description("应用版本")
    @Option.Configured
    String version();

    @Description("应用环境")
    @Option.Configured
    @Option.Required
    @AllowedValue(value = "dev", description = "开发环境，开发者本地或共享编码调试环境")
    @AllowedValue(value = "sit", description = "系统集成测试环境，验证多个模块/服务间的交互")
    @AllowedValue(value = "uat", description = "用户验收测试环境，业务方验证功能是否符合需求")
    @AllowedValue(value = "stg", description = "预发布环境，功能与[PROD]一致，用于最终验证")
    @AllowedValue(value = "prod", description = "生产环境，面向真实用户的线上环境")
    String env();
}

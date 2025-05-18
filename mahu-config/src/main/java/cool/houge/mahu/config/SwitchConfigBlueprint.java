package cool.houge.mahu.config;

import cool.houge.mahu.config.annotation.Refreshable;
import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

/// 应用开关配置
///
/// @author ZY (kzou227@qq.com)
@Refreshable
@Prototype.Blueprint
@Prototype.Configured
interface SwitchConfigBlueprint {

    /// 定时调度功能是否开启
    @Option.Configured
    boolean schedulerOn();
}

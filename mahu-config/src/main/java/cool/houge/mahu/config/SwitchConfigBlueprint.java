package cool.houge.mahu.config;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;
import io.sundr.transform.annotations.TemplateTransformation;

/// 应用开关配置
///
/// @author ZY (kzou227@qq.com)
@Prototype.Blueprint(builderPublic = false, createEmptyPublic = false)
@Prototype.Configured
@TemplateTransformation("/codegen/refresh-config.vm")
interface SwitchConfigBlueprint {

    /// 定时调度功能是否开启
    @Option.Configured
    boolean schedulerOn();
}

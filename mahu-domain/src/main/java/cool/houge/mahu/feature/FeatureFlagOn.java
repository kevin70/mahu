package cool.houge.mahu.feature;

import io.helidon.service.registry.Interception;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// 功能开关拦截注解。
///
/// 标注在方法或类上，表示调用前需要检查对应的功能开关是否处于生效状态。
/// 具体的检查逻辑由 `FeatureFlagInterceptor` 实现。
@Interception.Intercepted
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface FeatureFlagOn {

    /// 对应的功能开关 code（`sys.feature_flags.code`）。
    String value();
}

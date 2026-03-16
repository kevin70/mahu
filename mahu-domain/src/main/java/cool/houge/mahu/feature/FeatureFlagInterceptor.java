package cool.houge.mahu.feature;

import cool.houge.mahu.shared.service.AppSharedService;
import io.helidon.common.types.TypeName;
import io.helidon.service.registry.Interception;
import io.helidon.service.registry.InterceptionContext;
import io.helidon.service.registry.Service;
import lombok.AllArgsConstructor;

/// 功能开关拦截器。
///
/// 使用 {@link FeatureFlagOn} 注解的方法在执行前会检查对应开关是否处于生效状态，
/// 若开关未生效，则抛出 BizCodeException(BizCodes.PERMISSION_DENIED) 阻止调用。
@Service.Singleton
@Service.NamedByType(FeatureFlagOn.class)
@AllArgsConstructor
class FeatureFlagInterceptor implements Interception.Interceptor {

    private static final TypeName FEATURE_FLAG_ON_TYPENAME = TypeName.create(FeatureFlagOn.class);

    private final AppSharedService appSharedService;

    @Override
    public <V> V proceed(InterceptionContext ctx, Chain<V> chain, Object... args) throws Exception {
        var codeOpt = ctx.elementInfo()
                .annotation(FEATURE_FLAG_ON_TYPENAME)
                .stringValue();

        if (codeOpt.isEmpty()) {
            return chain.proceed(args);
        }

        appSharedService.ensureFeatureFlagOn(codeOpt.get());
        return chain.proceed(args);
    }
}

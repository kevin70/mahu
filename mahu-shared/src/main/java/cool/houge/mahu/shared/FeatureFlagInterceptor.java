package cool.houge.mahu.shared;

import cool.houge.mahu.shared.service.AppSharedService;
import io.helidon.common.types.TypeName;
import io.helidon.service.registry.Interception;
import io.helidon.service.registry.InterceptionContext;
import io.helidon.service.registry.Service;
import lombok.AllArgsConstructor;

@Service.Singleton
@Service.NamedByType(FeatureFlagOn.class)
@AllArgsConstructor
class FeatureFlagInterceptor implements Interception.Interceptor {

    private static final TypeName FEATURE_FLAG_ON_TYPENAME = TypeName.create(FeatureFlagOn.class);

    private final AppSharedService appSharedService;

    @Override
    public <V> V proceed(InterceptionContext ctx, Chain<V> chain, Object... args) throws Exception {
        var codeOpt = ctx.elementInfo().annotation(FEATURE_FLAG_ON_TYPENAME).stringValue();

        if (codeOpt.isEmpty()) {
            return chain.proceed(args);
        }

        appSharedService.ensureFeatureFlagOn(codeOpt.get());
        return chain.proceed(args);
    }
}

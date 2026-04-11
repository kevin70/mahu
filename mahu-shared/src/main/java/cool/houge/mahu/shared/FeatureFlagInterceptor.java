package cool.houge.mahu.shared;

import cool.houge.mahu.shared.service.PlatformSharedService;
import io.helidon.common.types.TypeName;
import io.helidon.service.registry.Interception;
import io.helidon.service.registry.InterceptionContext;
import io.helidon.service.registry.Service;
import lombok.AllArgsConstructor;

/// 功能开关拦截器
///
/// 实现 {@link FeatureFlagOn} 注解的拦截逻辑。
/// 在被注解的方法执行前检查对应的功能开关是否启用，
/// 若未启用则抛出异常，阻止方法执行。
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@Service.NamedByType(FeatureFlagOn.class)
@AllArgsConstructor
class FeatureFlagInterceptor implements Interception.Interceptor {

    private static final TypeName FEATURE_FLAG_ON_TYPENAME = TypeName.create(FeatureFlagOn.class);

    private final PlatformSharedService platformSharedService;

    /// 执行拦截逻辑：检查功能开关是否启用
    ///
    /// @param ctx 拦截上下文
    /// @param chain 拦截链
    /// @param args 方法参数
    /// @return 方法执行结果
    /// @throws Exception 若功能开关未启用或方法执行异常
    @Override
    public <V> V proceed(InterceptionContext ctx, Chain<V> chain, Object... args) throws Exception {
        var codeOpt = ctx.elementInfo().annotation(FEATURE_FLAG_ON_TYPENAME).stringValue();

        if (codeOpt.isEmpty()) {
            return chain.proceed(args);
        }

        platformSharedService.ensureFeatureFlagOn(codeOpt.get());
        return chain.proceed(args);
    }
}

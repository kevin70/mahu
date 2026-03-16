package cool.houge.mahu.feature;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.shared.ImmutableFeatureFlag;
import cool.houge.mahu.shared.service.AppSharedService;
import io.helidon.common.types.TypeName;
import io.helidon.service.registry.Interception;
import io.helidon.service.registry.InterceptionContext;
import io.helidon.service.registry.Service;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 功能开关拦截器。
///
/// 使用 {@link FeatureFlagOn} 注解的方法在执行前会检查对应开关是否处于生效状态，
/// 若开关未生效，则抛出 BizCodeException(BizCodes.PERMISSION_DENIED) 阻止调用。
@Service.Singleton
@Service.NamedByType(FeatureFlagOn.class)
@AllArgsConstructor
class FeatureFlagInterceptor implements Interception.Interceptor {

    private static final Logger log = LogManager.getLogger(FeatureFlagInterceptor.class);
    private static final TypeName FEATURE_FLAG_ON_TYPENAME = TypeName.create(FeatureFlagOn.class);

    private final AppSharedService appSharedService;

    @Override
    public <V> V proceed(InterceptionContext ctx, Chain<V> chain, Object... args) throws Exception {
        Optional<String> codeOpt = ctx.elementInfo()
                .annotation(FEATURE_FLAG_ON_TYPENAME)
                .stringValue()
                .filter(code -> !code.isEmpty());

        codeOpt.ifPresent(code -> {
            ImmutableFeatureFlag flag;
            try {
                flag = appSharedService.getFeatureFlag(code);
            } catch (BizCodeException e) {
                // DATA_LOSS 等错误向上抛，由上层统一处理
                log.error("加载功能开关失败 code={}", code);
                throw e;
            } catch (Exception e) {
                log.error("加载功能开关发生未知错误 code={}", code, e);
                throw new BizCodeException(BizCodes.INTERNAL, "加载功能开关失败: " + code);
            }

            if (!flag.isActive()) {
                log.debug("功能开关未生效，阻止调用: code={}", code);
                throw new BizCodeException(BizCodes.PERMISSION_DENIED, "功能未开启: " + code);
            }
        });
        return chain.proceed(args);
    }
}

package cool.houge.mahu.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import cool.houge.mahu.shared.service.PlatformSharedService;
import io.helidon.common.types.Annotation;
import io.helidon.common.types.TypedElementInfo;
import io.helidon.service.registry.Interception;
import io.helidon.service.registry.InterceptionContext;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/// {@link FeatureFlagInterceptor} 单元测试。
class FeatureFlagInterceptorTest {

    private final PlatformSharedService platformSharedService = mock(PlatformSharedService.class);
    private final FeatureFlagInterceptor interceptor = new FeatureFlagInterceptor(platformSharedService);

    @Test
    void proceed_checks_feature_flag_before_invoking_chain() throws Exception {
        var ctx = mock(InterceptionContext.class);
        var elementInfo = mock(TypedElementInfo.class);
        var annotation = mock(Annotation.class);
        @SuppressWarnings("unchecked")
        var chain = (Interception.Interceptor.Chain<String>) mock(Interception.Interceptor.Chain.class);
        var args = new Object[] {"hello", 1};

        when(ctx.elementInfo()).thenReturn(elementInfo);
        when(elementInfo.annotation(any())).thenReturn(annotation);
        when(annotation.stringValue()).thenReturn(Optional.of("pay.wechat"));
        when(chain.proceed(args)).thenReturn("ok");

        var result = interceptor.proceed(ctx, chain, args);

        assertEquals("ok", result);
        verify(platformSharedService).ensureFeatureFlagOn("pay.wechat");
        verify(chain).proceed(args);
    }

    @Test
    void proceed_skips_feature_flag_check_when_annotation_value_missing() throws Exception {
        var ctx = mock(InterceptionContext.class);
        var elementInfo = mock(TypedElementInfo.class);
        var annotation = mock(Annotation.class);
        @SuppressWarnings("unchecked")
        var chain = (Interception.Interceptor.Chain<String>) mock(Interception.Interceptor.Chain.class);
        var args = new Object[] {"hello"};

        when(ctx.elementInfo()).thenReturn(elementInfo);
        when(elementInfo.annotation(any())).thenReturn(annotation);
        when(annotation.stringValue()).thenReturn(Optional.empty());
        when(chain.proceed(args)).thenReturn("ok");

        var result = interceptor.proceed(ctx, chain, args);

        assertEquals("ok", result);
        verifyNoInteractions(platformSharedService);
        verify(chain).proceed(args);
    }
}

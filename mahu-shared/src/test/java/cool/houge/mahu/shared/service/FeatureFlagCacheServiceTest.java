package cool.houge.mahu.shared.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.github.benmanes.caffeine.cache.Cache;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.repository.sys.FeatureFlagRepository;
import cool.houge.mahu.shared.ImmutableFeatureFlag;
import io.helidon.service.registry.Services;
import io.helidon.testing.junit5.Testing;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/// {@link FeatureFlagCacheService} 单元测试。
///
/// 通过 {@link Services#set} 在每个测试方法的 Registry 中注入 {@link FeatureFlagRepository} mock，
/// 再由 Helidon DI 将 {@link FeatureFlagCacheService} 作为方法参数注入。
/// Config 依赖由 Registry 自动提供（测试环境无配置文件时降级为空节点，FixedRate 使用默认值）。
@Testing.Test(perMethod = true)
class FeatureFlagCacheServiceTest {

    private final FeatureFlagRepository featureFlagRepository = mock(FeatureFlagRepository.class);

    /// 在 FeatureFlagCacheService 被首次请求前，将 FeatureFlagRepository mock 注入当前方法的 Registry。
    @BeforeEach
    void setUp() {
        Services.set(FeatureFlagRepository.class, featureFlagRepository);
    }

    @Test
    void loadFeature_returns_cached_flag(FeatureFlagCacheService service) throws Exception {
        var cached = ImmutableFeatureFlag.builder()
                .id(-101)
                .code("pay.wechat")
                .name("微信支付")
                .description("d")
                .enabled(true)
                .preset(false)
                .ordering(10)
                .build();
        featureCache(service).put("pay.wechat", cached);

        var loaded = service.loadFeature("pay.wechat");

        assertSame(cached, loaded);
    }

    @Test
    void loadFeature_throws_data_loss_when_missing_in_cache(FeatureFlagCacheService service) {
        var ex = assertThrows(BizCodeException.class, () -> service.loadFeature("missing"));

        assertEquals(BizCodes.DATA_LOSS, ex.getCode());
    }

    /// 通过反射获取 FeatureFlagCacheService 内部的 Caffeine 缓存实例，用于测试中预填缓存数据。
    @SuppressWarnings("unchecked")
    private static Cache<String, ImmutableFeatureFlag> featureCache(FeatureFlagCacheService service) throws Exception {
        Field field = FeatureFlagCacheService.class.getDeclaredField("featureCache");
        field.setAccessible(true);
        return (Cache<String, ImmutableFeatureFlag>) field.get(service);
    }
}

package cool.houge.mahu.shared.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.entity.sys.FeatureFlag;
import cool.houge.mahu.repository.sys.FeatureFlagRepository;
import cool.houge.mahu.shared.ImmutableFeatureFlag;
import io.ebean.annotation.Transactional;
import io.helidon.config.Config;
import io.helidon.scheduling.FixedRate;
import io.helidon.service.registry.Service;
import java.time.Duration;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;

/// 功能开关本地缓存服务。
///
/// 在应用启动时从数据库全量预热 `sys.feature_flags`，并通过定时任务持续刷新，
/// 供 `AppSharedService` 与 `@FeatureFlagOn` 拦截器以低延迟方式读取功能状态。
@Service.RunLevel(Service.RunLevel.STARTUP)
@Service.Singleton
@AllArgsConstructor
class FeatureFlagCacheService {

    private static final Logger log = LogManager.getLogger(FeatureFlagCacheService.class);

    private final Config config;
    private final FeatureFlagRepository featureFlagRepository;

    /// 功能开关缓存：key 为 `code`，value 为不可变快照对象。
    private final Cache<String, ImmutableFeatureFlag> featureCache = Caffeine.newBuilder()
            .recordStats()
            .expireAfterWrite(Duration.ofHours(2))
            .build();

    /// 启动时预热全部功能开关，并注册固定频率刷新任务。
    @Service.PostConstruct
    void init() {
        refreshAll();
        log.debug("功能全量缓存刷新完成");

        FixedRate.builder()
                .interval(Duration.ofMinutes(10))
                .delayBy(Duration.ofMinutes(10))
                .config(config.get("scheduling.feature-flag-cache-refresh"))
                .task(inv -> {
                    try {
                        refreshAll();
                    } catch (Exception e) {
                        log.error("定时刷新功能缓存失败", e);
                    }
                })
                .build();
    }

    /// 从本地缓存读取功能开关。
    ///
    /// 这里约定缓存未命中视为数据缺失，而不是自动回源查询，
    /// 这样可以保证所有读路径都依赖同一份定时刷新的快照。
    @NonNull
    ImmutableFeatureFlag loadFeature(String code) {
        var flag = featureCache.getIfPresent(code);
        if (flag == null) {
            throw new BizCodeException(BizCodes.DATA_LOSS, "缺少功能: %s", code);
        }
        return flag;
    }

    /// 将数据库实体转换为不可变缓存模型。
    private ImmutableFeatureFlag map(FeatureFlag bean) {
        return ImmutableFeatureFlag.builder()
                .id(bean.getId())
                .code(bean.getCode())
                .name(bean.getName())
                .description(bean.getDescription())
                .enabled(bean.isEnabled())
                .preset(bean.isPreset())
                .enableAt(bean.getEnableAt())
                .disableAt(bean.getDisableAt())
                .ordering(bean.getOrdering())
                .build();
    }

    /// 全量刷新功能开关缓存。
    ///
    /// 当前实现按查询结果逐条覆盖缓存，适合功能开关体量较小、读多写少的场景。
    @Transactional(readOnly = true)
    private void refreshAll() {
        var all = featureFlagRepository.findAll();
        for (FeatureFlag feature : all) {
            var b = map(feature);
            featureCache.put(feature.getCode(), b);
        }
    }
}

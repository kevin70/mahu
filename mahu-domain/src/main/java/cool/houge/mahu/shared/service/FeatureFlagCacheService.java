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

/// 功能开关缓存服务
///
/// 负责将功能开关配置从数据库加载到内存缓存中，并支持定时全量刷新。
/// 刷新周期与启停可通过 `scheduling.feature-flag-cache-refresh` 配置调整。
@Service.RunLevel(Service.RunLevel.STARTUP)
@Service.Singleton
@AllArgsConstructor
class FeatureFlagCacheService {

    private static final Logger log = LogManager.getLogger(FeatureFlagCacheService.class);

    private final Config config;
    private final FeatureFlagRepository featureFlagRepository;

    private final Cache<String, ImmutableFeatureFlag> featureCache = Caffeine.newBuilder()
            .recordStats()
            .expireAfterWrite(Duration.ofHours(2))
            .build();

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

    /// 根据功能 code 获取功能开关
    @NonNull
    ImmutableFeatureFlag loadFeature(String code) {
        var flag = featureCache.getIfPresent(code);
        if (flag == null) {
            throw new BizCodeException(BizCodes.DATA_LOSS, "缺少功能: %s", code);
        }
        return flag;
    }

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

    @Transactional(readOnly = true)
    private void refreshAll() {
        var all = featureFlagRepository.findAll();
        for (FeatureFlag feature : all) {
            var b = map(feature);
            featureCache.put(feature.getCode(), b);
        }
    }
}

package cool.houge.mahu.shared.service;

import static java.util.Optional.ofNullable;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.Status;
import cool.houge.mahu.entity.sys.Feature;
import cool.houge.mahu.repository.sys.FeatureRepository;
import cool.houge.mahu.shared.ImmutableFeature;
import cool.houge.mahu.util.RoaringBitmapUtils;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;

/// 功能
///
/// @author ZY (kzou227@qq.com)
@Service.RunLevel(Service.RunLevel.STARTUP)
@Service.Singleton
@AllArgsConstructor
class FeatureHelper {

    private static final Logger log = LogManager.getLogger(FeatureHelper.class);
    private final Cache<Integer, ImmutableFeature> featureCache = Caffeine.newBuilder()
            .recordStats()
            .expireAfterWrite(Duration.ofDays(1))
            .build();
    private final FeatureRepository featureRepository;

    /// 获取指定的功能
    @SuppressWarnings("DataFlowIssue")
    @NonNull
    ImmutableFeature loadFeature(int featureId) {
        return featureCache.get(featureId, this::getFeature);
    }

    private ImmutableFeature map(Feature bean) {
        return ImmutableFeature.builder()
                .id(bean.getId())
                .module(bean.getModule())
                .code(bean.getCode())
                .name(bean.getName())
                .description(bean.getDescription())
                .status(bean.getStatus())
                .effectiveFrom(bean.getEffectiveFrom())
                .effectiveTo(bean.getEffectiveTo())
                .startTime(bean.getStartTime())
                .endTime(bean.getEndTime())
                .weekdays(ofNullable(bean.getWeekdays()).map(List::copyOf).orElse(List.of()))
                .allowUserRb(RoaringBitmapUtils.toRoaring64NavigableMap(bean.getAllowUserRb()))
                .denyUserRb(RoaringBitmapUtils.toRoaring64NavigableMap(bean.getDenyUserRb()))
                .properties(ofNullable(bean.getProperties()).map(Map::copyOf).orElse(Map.of()))
                .propertiesSchema(
                        ofNullable(bean.getPropertiesSchema()).map(Map::copyOf).orElse(Map.of()))
                .build();
    }

    @Transactional(readOnly = true)
    private ImmutableFeature getFeature(int featureId) {
        var dbBean = featureRepository.findById(featureId);
        if (dbBean == null) {
            throw new BizCodeException(BizCodes.DATA_LOSS, "缺少功能: %s", featureId);
        }
        return map(dbBean);
    }

    @Transactional(readOnly = true)
    private void refreshAll() {
        var all = featureRepository.findAll();
        for (Feature feature : all) {
            if (Status.ARCHIVED.eq(feature.getStatus())) {
                continue;
            }

            var b = map(feature);
            featureCache.put(feature.getId(), b);
        }
        log.debug("功能全量缓存刷新完成");
    }
}

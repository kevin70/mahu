package cool.houge.mahu.shared.service;

import static java.util.Optional.ofNullable;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.StatusCodes;
import cool.houge.mahu.entity.sys.Feature;
import cool.houge.mahu.shared.G;
import cool.houge.mahu.shared.LcFeature;
import cool.houge.mahu.shared.repository.sys.FeatureRepository;
import cool.houge.mahu.util.RoaringBitmapUtils;
import io.helidon.service.registry.Service;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;

/// 功能
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@AllArgsConstructor
class FeatureHelper {

    private final Supplier<Map<Integer, LcFeature>> featureConfigs =
            Suppliers.memoizeWithExpiration(this::loadFeatureMap, G.CACHE_5M_TTL);
    private final FeatureRepository featureRepository;

    /// 获取指定的功能
    @NonNull
    LcFeature loadFeature(int featureId) {
        var f = featureConfigs.get().get(featureId);
        if (f == null) {
            throw new BizCodeException(BizCodes.DATA_LOSS, "未找到 feature: [%s]", featureId);
        }
        return f;
    }

    private Map<Integer, LcFeature> loadFeatureMap() {
        return loadAll().stream().collect(ImmutableMap.toImmutableMap(LcFeature::getId, Function.identity()));
    }

    private List<@NonNull LcFeature> loadAll() {
        return featureRepository.findAll().stream()
                .filter(o -> !Objects.equals(StatusCodes.ARCHIVED, o.getStatus()))
                .map(this::map)
                .toList();
    }

    private LcFeature map(Feature bean) {
        return LcFeature.builder()
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
                .extraProperties(
                        ofNullable(bean.getExtraProperties()).map(Map::copyOf).orElse(Map.of()))
                .extraSchema(ofNullable(bean.getExtraSchema()).map(Map::copyOf).orElse(Map.of()))
                .build();
    }
}

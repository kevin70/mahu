package cool.houge.mahu.shared.service;

import static java.util.Optional.ofNullable;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.StatusCodes;
import cool.houge.mahu.entity.sys.Feature;
import cool.houge.mahu.shared.FeatureConfig;
import cool.houge.mahu.shared.G;
import cool.houge.mahu.shared.repository.sys.FeatureRepository;
import io.helidon.service.registry.Service;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.roaringbitmap.longlong.Roaring64NavigableMap;

/// 基础
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@AllArgsConstructor
public class SharedBaseService {

    private final Supplier<Map<Integer, FeatureConfig>> featureConfigs =
            Suppliers.memoizeWithExpiration(this::loadFeatureMap, G.CACHE_5M_TTL);

    private final FeatureRepository featureRepository;

    /// 判断是否为活跃功能
    public boolean isActiveFeature(int featureId) {
        return getFeature(featureId).isActive();
    }

    /// 获取指定的功能
    public @NonNull FeatureConfig getFeature(int featureId) {
        var f = featureConfigs.get().get(featureId);
        if (f == null) {
            throw new BizCodeException(BizCodes.DATA_LOSS, "未找到 feature: [%s]", featureId);
        }
        return f;
    }

    private Map<Integer, FeatureConfig> loadFeatureMap() {
        return loadAll().stream().collect(ImmutableMap.toImmutableMap(FeatureConfig::id, Function.identity()));
    }

    private List<@NonNull FeatureConfig> loadAll() {
        return featureRepository.findAll().stream()
                .filter(o -> !Objects.equals(StatusCodes.ARCHIVED, o.getStatus()))
                .map(this::map)
                .toList();
    }

    private FeatureConfig map(Feature bean) {
        return FeatureConfig.builder()
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
                .allowUserRb(toRoaring64Bitmap(bean.getAllowUserRb()))
                .denyUserRb(toRoaring64Bitmap(bean.getDenyUserRb()))
                .extraProperties(
                        ofNullable(bean.getExtraProperties()).map(Map::copyOf).orElse(Map.of()))
                .extraSchema(ofNullable(bean.getExtraSchema()).map(Map::copyOf).orElse(Map.of()))
                .build();
    }

    private Roaring64NavigableMap toRoaring64Bitmap(byte[] bytes) {
        var r = new Roaring64NavigableMap();
        if (bytes == null || bytes.length == 0) {
            return r;
        }

        try {
            r.deserialize(new DataInputStream(new ByteArrayInputStream(bytes)));
            return r;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

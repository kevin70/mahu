package cool.houge.mahu.shared.service;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.StatusCodes;
import cool.houge.mahu.entity.sys.DelayMessage;
import cool.houge.mahu.shared.FeatureConfig;
import cool.houge.mahu.shared.G;
import cool.houge.mahu.shared.repository.sys.DelayMessageRepository;
import cool.houge.mahu.shared.repository.sys.FeatureRepository;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;

/// 基础
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@AllArgsConstructor
public class SharedBaseService {

    private static final Logger log = LogManager.getLogger(SharedBaseService.class);
    private final Supplier<Map<Integer, FeatureConfig>> featureConfigs =
            Suppliers.memoizeWithExpiration(this::loadFeatureMap, G.CACHE_5M_TTL);

    private final FeatureRepository featureRepository;
    private final DelayMessageRepository delayMessageRepository;

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

    /// 推送延迟消息
    @Transactional
    public void pushDelayMessage(DelayMessage message) {
        delayMessageRepository.persist(message);
    }

    private Map<Integer, FeatureConfig> loadFeatureMap() {
        return loadAll().stream().collect(ImmutableMap.toImmutableMap(FeatureConfig::id, Function.identity()));
    }

    private List<@NonNull FeatureConfig> loadAll() {
        return featureRepository.findAll().stream()
                .filter(o -> !Objects.equals(StatusCodes.ARCHIVED, o.getStatus()))
                .map(FeatureConfig::of)
                .toList();
    }
}

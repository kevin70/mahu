package cool.houge.mahu.shared.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.entity.Dict;
import cool.houge.mahu.entity.DictGroup;
import cool.houge.mahu.repository.DictGroupRepository;
import cool.houge.mahu.repository.DictRepository;
import cool.houge.mahu.shared.ImmutableDict;
import cool.houge.mahu.shared.ImmutableDictGroup;
import io.ebean.annotation.Transactional;
import io.helidon.config.Config;
import io.helidon.scheduling.FixedRate;
import io.helidon.service.registry.Service;
import java.time.Duration;
import java.util.Collection;
import java.util.Comparator;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;

@Service.RunLevel(Service.RunLevel.STARTUP)
@Service.Singleton
@AllArgsConstructor
class DictCacheService {

    private static final Logger log = LogManager.getLogger(DictCacheService.class);

    private final Config config;
    private final DictGroupRepository dictGroupRepository;
    private final DictRepository dictRepository;

    private final Cache<String, ImmutableDictGroup> dictTypeCache = Caffeine.newBuilder()
            .recordStats()
            .expireAfterWrite(Duration.ofHours(2))
            .build();
    private final Cache<Integer, ImmutableDict> dictCache = Caffeine.newBuilder()
            .recordStats()
            .expireAfterWrite(Duration.ofHours(2))
            .build();

    @Service.PostConstruct
    void init() {
        this.refreshAll();
        log.debug("字典全量缓存刷新完成");

        FixedRate.builder()
                .interval(Duration.ofMinutes(10))
                .delayBy(Duration.ofMinutes(10))
                .config(config.get("scheduling.dict-cache-refresh"))
                .task((invocation) -> {
                    try {
                        refreshAll();
                    } catch (Exception e) {
                        log.error("定时刷新字典缓存失败", e);
                    }
                })
                .build();
    }

    Collection<ImmutableDictGroup> allDictTypes() {
        return dictTypeCache.asMap().values();
    }

    @NonNull
    ImmutableDictGroup loadDictType(String typeId) {
        return dictTypeCache.get(typeId, this::getDictType);
    }

    @NonNull
    ImmutableDict loadDict(int dc) {
        return dictCache.get(dc, this::getDict);
    }

    private ImmutableDictGroup map(DictGroup bean) {
        return ImmutableDictGroup.builder()
                .id(bean.getId())
                .name(bean.getName())
                .description(bean.getDescription())
                .enabled(bean.getEnabled())
                .preset(bean.isPreset())
                .dicts(bean.getData().stream()
                        .map(this::map)
                        .sorted(Comparator.comparing(ImmutableDict::getOrdering).reversed())
                        .toList())
                .build();
    }

    private ImmutableDict map(Dict bean) {
        return ImmutableDict.builder()
                .groupId(bean.getGroup().getId())
                .dc(bean.getDc())
                .value(bean.getValue())
                .label(bean.getLabel())
                .enabled(bean.getEnabled())
                .ordering(bean.getOrdering())
                .color(bean.getColor())
                .preset(bean.isPreset())
                .build();
    }

    @Transactional(readOnly = true)
    @NonNull
    private ImmutableDictGroup getDictType(String dictGroupId) {
        var dbDictGroup = dictGroupRepository.findById(dictGroupId);
        if (dbDictGroup == null) {
            throw new BizCodeException(BizCodes.DATA_LOSS, "缺少字典分组: %s", dictGroupId);
        }
        return map(dbDictGroup);
    }

    @Transactional(readOnly = true)
    @NonNull
    private ImmutableDict getDict(int dc) {
        var dbDict = dictRepository.findById(dc);
        if (dbDict == null) {
            throw new BizCodeException(BizCodes.DATA_LOSS, "缺少字典: %s", dc);
        }
        return map(dbDict);
    }

    @Transactional(readOnly = true)
    private void refreshAll() {
        var all = dictGroupRepository.findAllData();
        for (DictGroup dictGroup : all) {
            var lcType = map(dictGroup);
            dictTypeCache.put(lcType.getId(), lcType);

            for (ImmutableDict dict : lcType.getDicts()) {
                dictCache.put(dict.getDc(), dict);
            }
        }
    }
}

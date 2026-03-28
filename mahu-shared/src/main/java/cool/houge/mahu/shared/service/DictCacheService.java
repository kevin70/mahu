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

/// 字典缓存服务
///
/// 使用 Caffeine 本地缓存管理字典分组和字典数据，提高频繁访问的性能。
/// 在应用启动时全量加载，每 10 分钟自动刷新一次。
/// 提供字典查询接口给业务层使用。
///
/// @author ZY (kzou227@qq.com)
@Service.RunLevel(Service.RunLevel.STARTUP)
@Service.Singleton
@AllArgsConstructor
class DictCacheService {

    private static final Logger log = LogManager.getLogger(DictCacheService.class);

    private final Config config;
    private final DictGroupRepository dictGroupRepository;
    private final DictRepository dictRepository;

    /// 字典分组缓存（ID → ImmutableDictGroup）
    private final Cache<String, ImmutableDictGroup> dictTypeCache = Caffeine.newBuilder()
            .recordStats()
            .expireAfterWrite(Duration.ofHours(2))
            .build();

    /// 字典缓存（字典代码 → ImmutableDict）
    private final Cache<Integer, ImmutableDict> dictCache = Caffeine.newBuilder()
            .recordStats()
            .expireAfterWrite(Duration.ofHours(2))
            .build();

    /// 初始化缓存：全量加载字典数据，并启动定时刷新任务
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

    /// 获取所有字典分组
    /// @return 所有缓存中的字典分组集合
    Collection<ImmutableDictGroup> allDictTypes() {
        return dictTypeCache.asMap().values();
    }

    /// 查询字典分组（缓存优先）
    /// @param typeId 字典分组 ID
    /// @return 对应的不可变字典分组对象
    @NonNull
    ImmutableDictGroup loadDictType(String typeId) {
        return dictTypeCache.get(typeId, this::getDictType);
    }

    /// 查询单个字典数据（缓存优先）
    /// @param dc 字典代码
    /// @return 对应的不可变字典对象
    @NonNull
    ImmutableDict loadDict(int dc) {
        return dictCache.get(dc, this::getDict);
    }

    /// 将 DictGroup 数据库实体转换为不可变对象
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

    /// 将 Dict 数据库实体转换为不可变对象
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

    /// 从数据库查询字典分组
    @Transactional(readOnly = true)
    @NonNull
    private ImmutableDictGroup getDictType(String dictGroupId) {
        var dbDictGroup = dictGroupRepository.findById(dictGroupId);
        if (dbDictGroup == null) {
            throw new BizCodeException(BizCodes.DATA_LOSS, "缺少字典分组: %s", dictGroupId);
        }
        return map(dbDictGroup);
    }

    /// 从数据库查询单个字典
    @Transactional(readOnly = true)
    @NonNull
    private ImmutableDict getDict(int dc) {
        var dbDict = dictRepository.findById(dc);
        if (dbDict == null) {
            throw new BizCodeException(BizCodes.DATA_LOSS, "缺少字典: %s", dc);
        }
        return map(dbDict);
    }

    /// 刷新所有缓存：从数据库全量加载字典分组和字典数据
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

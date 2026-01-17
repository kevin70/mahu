package cool.houge.mahu.shared.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.Env;
import cool.houge.mahu.entity.Dict;
import cool.houge.mahu.entity.DictType;
import cool.houge.mahu.shared.ImmutableDict;
import cool.houge.mahu.shared.ImmutableDictType;
import cool.houge.mahu.shared.repository.DictRepository;
import cool.houge.mahu.shared.repository.DictTypeRepository;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service;
import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;

/// 字典帮助类
///
/// @author ZY (kzou227@qq.com)
@Service.RunLevel(Service.RunLevel.STARTUP)
@Service.Singleton
@AllArgsConstructor
class DicHelper {

    private static final Logger log = LogManager.getLogger(DicHelper.class);
    private final DictTypeRepository dictTypeRepository;
    private final DictRepository dictRepository;

    private final Cache<String, ImmutableDictType> dictTypeCache = Caffeine.newBuilder()
            .recordStats()
            .expireAfterWrite(Duration.ofDays(1))
            .build();
    private final Cache<Integer, ImmutableDict> dictCache = Caffeine.newBuilder()
            .recordStats()
            .expireAfterWrite(Duration.ofDays(1))
            .build();

    @Service.PostConstruct
    void init() {
        // var delay = Env.current().isProd() ? Duration.ofMinutes(10) : Duration.ofMinutes(1);
        // SCHEDULED_EXECUTOR
        //         .get()
        //         .scheduleWithFixedDelay(this::refreshAll, delay.toMillis(), delay.toMillis(), TimeUnit.MILLISECONDS);
        // this.refreshAll();
    }

    Collection<ImmutableDictType> allDictTypes() {
        return dictTypeCache.asMap().values();
    }

    @SuppressWarnings("DataFlowIssue")
    @NonNull
    ImmutableDictType loadDictType(String typeId) {
        return dictTypeCache.get(typeId, this::getDictType);
    }

    @SuppressWarnings("DataFlowIssue")
    @NonNull
    ImmutableDict loadDict(int dc) {
        return dictCache.get(dc, this::getDict);
    }

    private ImmutableDictType map(DictType bean) {
        return ImmutableDictType.builder()
                .id(bean.getId())
                .name(bean.getName())
                .description(bean.getDescription())
                .disabled(bean.getDisabled())
                .dicts(bean.getData().stream().map(this::map).toList())
                .build();
    }

    private ImmutableDict map(Dict bean) {
        return ImmutableDict.builder()
                .dc(bean.getDc())
                .value(bean.getValue())
                .label(bean.getLabel())
                .disabled(bean.getDisabled())
                .ordering(bean.getOrdering())
                .build();
    }

    @Transactional(readOnly = true)
    private ImmutableDictType getDictType(String dictTypeId) {
        var dbDictType = dictTypeRepository.findById(dictTypeId);
        if (dbDictType == null) {
            throw new BizCodeException(BizCodes.DATA_LOSS, "缺少字典类型: %s", dictTypeId);
        }
        return map(dbDictType);
    }

    @Transactional(readOnly = true)
    private ImmutableDict getDict(int dc) {
        var dbDict = dictRepository.findById(dc);
        if (dbDict == null) {
            throw new BizCodeException(BizCodes.DATA_LOSS, "缺少字典: %s", dc);
        }
        return map(dbDict);
    }

    @Transactional(readOnly = true)
    private void refreshAll() {
        var all = dictTypeRepository.findAllData();
        for (DictType dictType : all) {
            var lcType = map(dictType);
            dictTypeCache.put(lcType.getId(), lcType);

            for (ImmutableDict dict : lcType.getDicts()) {
                dictCache.put(dict.getDc(), dict);
            }
        }
        log.debug("字典全量缓存刷新完成");
    }
}

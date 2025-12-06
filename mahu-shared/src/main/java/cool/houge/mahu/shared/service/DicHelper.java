package cool.houge.mahu.shared.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.entity.Dict;
import cool.houge.mahu.entity.DictType;
import cool.houge.mahu.shared.LcDict;
import cool.houge.mahu.shared.LcDictType;
import cool.houge.mahu.shared.repository.DictRepository;
import cool.houge.mahu.shared.repository.DictTypeRepository;
import io.helidon.service.registry.Service;
import java.time.Duration;
import lombok.AllArgsConstructor;

/// 字典帮助类
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@AllArgsConstructor
class DicHelper {

    private final DictTypeRepository dictTypeRepository;
    private final DictRepository dictRepository;

    private final Cache<Integer, LcDictType> dictTypeCache = Caffeine.newBuilder()
            .recordStats()
            .expireAfterWrite(Duration.ofDays(1))
            .build();
    private final Cache<Integer, LcDict> dictCache = Caffeine.newBuilder()
            .recordStats()
            .expireAfterWrite(Duration.ofDays(1))
            .build();

    LcDictType loadDictType(int dictTypeId) {
        return dictTypeCache.get(dictTypeId, this::getDictType);
    }

    LcDict loadDict(int dc) {
        return dictCache.get(dc, this::getDict);
    }

    private LcDictType getDictType(int dictTypeId) {
        var dbDictType = dictTypeRepository.findById(dictTypeId);
        if (dbDictType == null) {
            throw new BizCodeException(BizCodes.DATA_LOSS, "缺少字典类型: %s", dictTypeId);
        }
        return map(dbDictType);
    }

    private LcDict getDict(int dc) {
        var dbDict = dictRepository.findById(dc);
        if (dbDict==null) {
            throw new BizCodeException(BizCodes.DATA_LOSS, "缺少字典: %s", dc);
        }
        return map(dbDict);
    }

    private LcDictType map(DictType bean) {
        return LcDictType.builder()
                .id(bean.getId())
                .name(bean.getName())
                .description(bean.getDescription())
                .disabled(bean.getDisabled())
                .dicts(bean.getData().stream().map(this::map).toList())
                .build();
    }

    private LcDict map(Dict bean) {
        return LcDict.builder()
                .dc(bean.getDc())
                .value(bean.getValue())
                .label(bean.getLabel())
                .disabled(bean.getDisabled())
                .ordering(bean.getOrdering())
                .build();
    }
}

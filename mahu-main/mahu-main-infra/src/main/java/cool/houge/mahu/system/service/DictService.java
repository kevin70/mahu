package cool.houge.mahu.system.service;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.entity.system.DictData;
import cool.houge.mahu.entity.system.DictType;
import cool.houge.mahu.system.repository.DictTypeRepository;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;

/// 字典
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class DictService {

    private final DictTypeRepository dictTypeRepository;

    @Inject
    public DictService(DictTypeRepository dictTypeRepository) {
        this.dictTypeRepository = dictTypeRepository;
    }

    /// 按照指定的字典类型查询数据
    /// @param typeCode 类型代码
    @Transactional(readOnly = true)
    public DictType findByTypeCode(String typeCode) {
        var bean = dictTypeRepository.findById(typeCode);
        if (bean == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND);
        }
        return bean;
    }

    /// 查询指定字典数据
    ///
    /// @param typeCode 字典类型代码
    /// @param dataCode 字典数据代码
    @Transactional(readOnly = true)
    public DictData findDictData(String typeCode, String dataCode) {
        var bean = dictTypeRepository.findDictData(typeCode, dataCode);
        if (bean == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND);
        }
        return bean;
    }

    /// 查询所有字典数据
    @Transactional(readOnly = true)
    public List<DictType> findAll() {
        return dictTypeRepository.findAll();
    }
}

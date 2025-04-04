package cool.houge.mahu.admin.system.service;

import com.google.common.collect.Lists;
import cool.houge.mahu.admin.bean.GeneralBeanMapper;
import cool.houge.mahu.admin.shared.SharedToolService;
import cool.houge.mahu.admin.system.repository.DictTypeRepository;
import cool.houge.mahu.common.BizCodeException;
import cool.houge.mahu.common.BizCodes;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.system.DictData;
import cool.houge.mahu.entity.system.DictType;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Objects;

/// 数据字典
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class DictService {

    @Inject
    DictTypeRepository dictTypeRepository;

    @Inject
    GeneralBeanMapper beanMapper;

    @Inject
    SharedToolService toolService;

    /// 保存字典数据
    @Transactional
    public void save(DictType bean) {
        dictTypeRepository.save(bean);
    }

    /// 更新字典数据
    @Transactional
    public void update(DictType bean) {
        var dbBean = findByTypeCode(bean.getTypeCode());
        beanMapper.map(dbBean, bean);

        if (dbBean.getData() == null) {
            dbBean.setData(bean.getData());
        } else {
            mergeDictData(dbBean.getData(), bean.getData());
        }
        dictTypeRepository.update(dbBean);
    }

    //    /// 更新字典
    //    @Transactional
    //    public void update(DictType bean) {
    //        dictTypeRepository.update(dict);
    //    }
    //
    //    /// 删除指定 ID 的字典数据
    //    /// @param id 字典数据
    //    @Transactional
    //    public void deleteById(Integer id) {
    //        dictTypeRepository.delete(new Dict().setId(id));
    //    }
    //
    //    /// 查询指定 ID 的字典数据
    //    @Transactional(readOnly = true)
    //    public Dict findById(Integer id) {
    //        return dictTypeRepository.findById(id);
    //    }
    //
    //    /// 查询指定名称与值的字典数据
    //    @Transactional(readOnly = true)
    //    public Dict findDictByKindValue(String name, String value) {
    //        return dictTypeRepository.findDictByKindValue(name, value);
    //    }

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

    /// 分页查询数据
    ///
    /// @param dataFilter 数据过滤
    @Transactional(readOnly = true)
    public PagedList<DictType> findPage(DataFilter dataFilter) {
        var plist = dictTypeRepository.findPage(dataFilter);
        return toolService.wrap(plist, dataFilter);
    }

    void mergeDictData(List<DictData> dbDictData, List<DictData> dictData) {
        for (DictData data : dictData) {
            var dbData = dbDictData.stream()
                    .filter(o -> Objects.equals(o.getDataCode(), data.getDataCode()))
                    .findFirst();
            if (dbData.isPresent()) {
                beanMapper.map(dbData.get(), data);
            } else {
                dbDictData.add(data);
            }
        }

        // 删除
        for (DictData dbData : Lists.newArrayList(dbDictData)) {
            var opt = dictData.stream()
                    .filter(o -> Objects.equals(o.getDataCode(), dbData.getDataCode()))
                    .findFirst();
            if (opt.isEmpty()) {
                dbDictData.remove(dbData);
            }
        }
    }
}

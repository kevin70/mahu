package cool.houge.mahu.admin.sys.service;

import com.google.common.collect.Lists;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.bean.EntityBeanMapper;
import cool.houge.mahu.admin.sys.repository.DictTypeRepository;
import cool.houge.mahu.entity.Dict;
import cool.houge.mahu.entity.DictType;
import cool.houge.mahu.domain.DataFilter;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 数据字典
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class DictService {

    private static final Logger log = LogManager.getLogger(DictService.class);

    private final DictTypeRepository dictTypeRepository;
    private final EntityBeanMapper beanMapper;

    /// 保存字典数据
    @Transactional
    public void save(DictType bean) {
        dictTypeRepository.save(bean);
    }

    /// 更新字典数据
    @Transactional
    public void update(DictType bean) {
        var dbBean = obtainById(bean.getType());
        beanMapper.map(dbBean, bean);

        if (dbBean.getData() == null) {
            dbBean.setData(bean.getData());
        } else {
            mergeDictData(dbBean.getData(), bean.getData());
        }
        dictTypeRepository.update(dbBean);
    }

    /// 删除指定代码的字典数据
    @Transactional
    public void deleteByTypeCode(String typeCode) {
        var bean = obtainById(typeCode);
        dictTypeRepository.delete(bean);
        log.info("删除数据字典 {}", bean);
    }

    /// 查询指定字典数据
    ///
    /// @param dataCode 字典数据代码
    @Transactional(readOnly = true)
    public Dict findDictData(String dataCode) {
        var bean = dictTypeRepository.findDictData(dataCode);
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
        return dictTypeRepository.findPage(dataFilter);
    }

    /// 查询指定代码的数据
    @Transactional(readOnly = true)
    public DictType findById(String typeCode) {
        return obtainById(typeCode);
    }

    /// 查询指定代码的数据，如果传入的 `typeCodes` 为 `null` 则返回所有数据
    @Transactional(readOnly = true)
    public List<DictType> findByTypeCodes(Collection<String> typeCodes) {
        return dictTypeRepository.findByTypeCodes(typeCodes);
    }

    private DictType obtainById(String typeCode) {
        var bean = dictTypeRepository.findById(typeCode);
        if (bean == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND);
        }
        return bean;
    }

    void mergeDictData(List<Dict> dbList, List<Dict> list) {
        for (Dict data : list) {
            var dbData = dbList.stream()
                    .filter(o -> Objects.equals(o.getCode(), data.getCode()))
                    .findFirst();
            if (dbData.isPresent()) {
                beanMapper.map(dbData.get(), data);
            } else {
                dbList.add(data);
            }
        }

        // 删除
        for (Dict dbData : Lists.newArrayList(dbList)) {
            var opt = list.stream()
                    .filter(o -> Objects.equals(o.getCode(), dbData.getCode()))
                    .findFirst();
            if (opt.isEmpty()) {
                dbList.remove(dbData);
            }
        }
    }
}

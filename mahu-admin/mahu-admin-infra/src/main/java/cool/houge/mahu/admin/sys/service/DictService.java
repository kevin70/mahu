package cool.houge.mahu.admin.sys.service;

import com.google.common.collect.Lists;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.bean.EntityBeanMapper;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.Dict;
import cool.houge.mahu.entity.DictGroup;
import cool.houge.mahu.query.DictQuery;
import cool.houge.mahu.repository.DictGroupRepository;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    private final EntityBeanMapper beanMapper;
    private final DictGroupRepository dictGroupRepository;

    /// 保存字典数据
    @Transactional
    public void save(DictGroup bean) {
        dictGroupRepository.save(bean);
    }

    /// 更新字典数据
    @Transactional
    public void update(DictGroup bean) {
        var dbBean = obtainById(bean.getId());
        beanMapper.map(dbBean, bean);

        if (dbBean.getData() == null) {
            dbBean.setData(bean.getData());
        } else {
            mergeDictData(dbBean.getData(), bean.getData());
        }
        dictGroupRepository.update(dbBean);
    }

    /// 删除指定代码的字典数据
    @Transactional
    public void deleteById(String id) {
        var bean = obtainById(id);
        if (bean.isPreset()) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "预置字典分组不可删除");
        }

        dictGroupRepository.delete(bean);
        log.info("删除数据字典 {}", bean);
    }

    /// 保存字典数据
    @Transactional
    public void save(String typeId, Dict bean) {
        var dbBean = obtainById(typeId);
        var list = Optional.ofNullable(dbBean.getData()).orElseGet(ArrayList::new);
        list.add(bean);
        dbBean.setData(list);
        dictGroupRepository.update(dbBean);
    }

    /// 查询指定字典数据
    ///
    /// @param dc 字典数据代码
    @Transactional(readOnly = true)
    public Dict findDictData(Integer dc) {
        var bean = dictGroupRepository.findDictData(dc);
        if (bean == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND);
        }
        return bean;
    }

    /// 分页查询数据
    @Transactional(readOnly = true)
    public PagedList<DictGroup> findPage(DictQuery query, Page page) {
        return dictGroupRepository.findPage(query, page);
    }

    /// 查询指定代码的数据
    @Transactional(readOnly = true)
    public DictGroup findById(String typeId) {
        return obtainById(typeId);
    }

    /// 查询指定代码的数据，如果传入的 `ids` 为 `null` 则返回所有数据
    @Transactional(readOnly = true)
    public List<DictGroup> findByIds(Collection<String> ids) {
        return dictGroupRepository.findByIds(ids);
    }

    private DictGroup obtainById(String typeId) {
        var bean = dictGroupRepository.findById(typeId);
        if (bean == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND);
        }
        return bean;
    }

    void mergeDictData(List<Dict> dbList, List<Dict> list) {
        for (Dict data : list) {
            var dbData = dbList.stream()
                    .filter(o -> Objects.equals(o.getDc(), data.getDc()))
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
                    .filter(o -> Objects.equals(o.getDc(), dbData.getDc()))
                    .findFirst();
            if (opt.isEmpty()) {
                dbList.remove(dbData);
            }
        }
    }
}

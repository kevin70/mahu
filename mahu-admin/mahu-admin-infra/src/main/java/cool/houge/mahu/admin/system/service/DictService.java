package cool.houge.mahu.admin.system.service;

import cool.houge.mahu.admin.shared.SharedToolService;
import cool.houge.mahu.admin.system.repository.DictRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.system.Dict;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 数据字典
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class DictService {

    @Inject
    DictRepository dictRepository;

    @Inject
    SharedToolService toolService;

    /// 保存字典
    @Transactional
    public void save(Dict dict) {
        dictRepository.save(dict);
    }

    /// 更新字典
    @Transactional
    public void update(Dict dict) {
        dictRepository.update(dict);
    }

    /// 删除指定 ID 的字典数据
    /// @param id 字典数据
    @Transactional
    public void deleteById(Integer id) {
        dictRepository.delete(new Dict().setId(id));
    }

    /// 查询指定 ID 的字典数据
    @Transactional(readOnly = true)
    public Dict findById(Integer id) {
        return dictRepository.findById(id);
    }

    /// 查询指定名称与值的字典数据
    @Transactional(readOnly = true)
    public Dict findDictByKindValue(String name, String value) {
        return dictRepository.findDictByKindValue(name, value);
    }

    /// 分页查询数据
    ///
    /// @param dataFilter 数据过滤
    @Transactional(readOnly = true)
    public PagedList<Dict> findPage(DataFilter dataFilter) {
        var plist = dictRepository.findPage(dataFilter);
        return toolService.wrap(plist, dataFilter);
    }
}

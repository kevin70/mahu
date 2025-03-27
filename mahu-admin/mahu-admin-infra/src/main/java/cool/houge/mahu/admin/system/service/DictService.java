package cool.houge.mahu.admin.system.service;

import cool.houge.mahu.admin.shared.SharedToolService;
import cool.houge.mahu.admin.system.repository.DictTypeRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.system.DictType;
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
    DictTypeRepository dictTypeRepository;

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
        //
    }

//
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
//
    /// 分页查询数据
    ///
    /// @param dataFilter 数据过滤
    @Transactional(readOnly = true)
    public PagedList<DictType> findPage(DataFilter dataFilter) {
        var plist = dictTypeRepository.findPage(dataFilter);
        return toolService.wrap(plist, dataFilter);
    }
}

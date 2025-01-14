package cool.houge.mahu.admin.market.service;

import cool.houge.mahu.admin.market.repository.AttributeRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.market.Attribute;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 商品属性
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AttributeService {

    @Inject
    AttributeRepository attributeRepository;

    /// 保存商品属性
    @Transactional
    public void save(Attribute attribute) {
        attribute.setDeleted(false);
        attributeRepository.save(attribute);
    }

    /// 更新商品属性
    @Transactional
    public void update(Attribute attribute) {
        attributeRepository.update(attribute);
    }

    /// 删除商品属性
    @Transactional
    public void delete(Attribute attribute) {
        attributeRepository.delete(attribute);
    }

    /// 查询指定 ID 商品属性
    @Transactional(readOnly = true)
    public Attribute findById(Integer id) {
        return attributeRepository.findById(id);
    }

    /// 分页查询商品属性
    @Transactional(readOnly = true)
    public PagedList<Attribute> findPage(DataFilter dataFilter) {
        return attributeRepository.findPage(dataFilter);
    }
}

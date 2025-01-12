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
        attributeRepository.save(attribute);
    }

    /// 分页查询商品属性
    @Transactional(readOnly = true)
    public PagedList<Attribute> findPage(DataFilter dataFilter) {
        return attributeRepository.findPage(dataFilter);
    }
}

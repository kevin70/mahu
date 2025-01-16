package cool.houge.mahu.admin.mart.service;

import cool.houge.mahu.admin.mart.repository.AttributeRepository;
import cool.houge.mahu.admin.mart.repository.AttributeValueRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.mart.Attribute;
import cool.houge.mahu.entity.mart.AttributeValue;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 商品属性
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AttributeService {

    private static final Logger log = LogManager.getLogger(AttributeService.class);

    @Inject
    AttributeRepository attributeRepository;

    @Inject
    AttributeValueRepository attributeValueRepository;

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
        var plist = attributeRepository.findPage(dataFilter);
        for (Attribute attribute : plist.getList()) {
            log.debug("加载属性{}的属性值: {}", attribute.getId(), attribute.getAttributeValues());
        }
        return plist;
    }

    /// 保存商品属性值
    @Transactional
    public void save(AttributeValue entity) {
        var dbEntity = attributeValueRepository.findByAttributeIdAndValue(
                entity.getAttribute().getId(), entity.getValue());
        if (dbEntity != null) {
            dbEntity.setDeleted(false).setValue(entity.getValue()).setOrdering(entity.getOrdering());
            attributeValueRepository.update(dbEntity);
        } else {
            entity.setDeleted(false);
            attributeValueRepository.save(entity);
        }
    }

    /// 删除商品属性值
    @Transactional
    public void delete(AttributeValue entity) {
        attributeValueRepository.delete(entity);
    }

    /// 修改商品属性值
    @Transactional
    public void update(AttributeValue entity) {
        attributeValueRepository.update(entity);
    }

    /// 查询指定 ID 的属性值
    @Transactional(readOnly = true)
    public AttributeValue findValueById(Integer id) {
        return attributeValueRepository.findById(id);
    }
}

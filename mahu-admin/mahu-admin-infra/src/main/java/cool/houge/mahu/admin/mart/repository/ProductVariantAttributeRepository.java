package cool.houge.mahu.admin.mart.repository;

import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.mart.ProductVariantAttribute;
import cool.houge.mahu.entity.mart.query.QProductVariantAttribute;
import io.ebean.Database;
import jakarta.inject.Singleton;

import java.util.List;

/// 产品变体属性
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ProductVariantAttributeRepository extends HBeanRepository<Long, ProductVariantAttribute> {

    public ProductVariantAttributeRepository(Database db) {
        super(ProductVariantAttribute.class, db);
    }

    /// 删除指定 ID 以外的产品属性
    public void deleteByProductId$NotInAttributeIds(long productId, List<Integer> attributeIds) {
        var qb = new QProductVariantAttribute().product.id.eq(productId);
        if (!attributeIds.isEmpty()) {
            qb.attribute.id.notIn(attributeIds);
        }
        qb.asUpdate().set(qb.deleted, true).update();
    }
}

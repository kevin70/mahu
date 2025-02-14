package cool.houge.mahu.admin.mart.repository;

import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.mart.ProductAttribute;
import cool.houge.mahu.entity.mart.query.QProductAttribute;
import io.ebean.Database;
import jakarta.inject.Singleton;

import java.util.List;

/// 产品属性
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ProductAttributeRepository extends HBeanRepository<Long, ProductAttribute> {

    public ProductAttributeRepository(Database db) {
        super(ProductAttribute.class, db);
    }

    /// 删除指定 ID 以外的产品属性
    public void deleteByProductId$NotInIds(long productId, List<Long> ids) {
        var qb = new QProductAttribute().product.id.eq(productId);
        if (!ids.isEmpty()) {
            qb.id.notIn(ids);
        }
        qb.asUpdate().set(qb.deleted, true).update();
    }
}

package cool.houge.mahu.admin.mart.repository;

import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.mart.ProductVariant;
import cool.houge.mahu.entity.mart.query.QProductVariant;
import io.ebean.Database;
import jakarta.inject.Singleton;

import java.util.List;

/// 产品变体
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ProductVariantRepository extends HBeanRepository<Long, ProductVariant> {

    public ProductVariantRepository(Database db) {
        super(ProductVariant.class, db);
    }

    /// 删除指定 ID 以外的产品属性
    public void deleteByProductId$NotInIds(long productId, List<Long> ids) {
        var qb = new QProductVariant().product.id.eq(productId);
        if (!ids.isEmpty()) {
            qb.id.notIn(ids);
        }
        qb.asUpdate().set(qb.deleted, true).update();
    }
}

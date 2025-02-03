package cool.houge.mahu.admin.mart.repository;

import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.mart.ProductVariantAttribute;
import io.ebean.Database;
import jakarta.inject.Singleton;

/// 产品变体属性
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ProductVariantAttributeRepository extends HBeanRepository<Long, ProductVariantAttribute> {

    public ProductVariantAttributeRepository(Database db) {
        super(ProductVariantAttribute.class, db);
    }
}

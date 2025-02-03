package cool.houge.mahu.admin.mart.repository;

import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.mart.ProductVariant;
import io.ebean.Database;
import jakarta.inject.Singleton;

/// 产品变体
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ProductVariantRepository extends HBeanRepository<Long, ProductVariant> {

    public ProductVariantRepository(Database db) {
        super(ProductVariant.class, db);
    }
}

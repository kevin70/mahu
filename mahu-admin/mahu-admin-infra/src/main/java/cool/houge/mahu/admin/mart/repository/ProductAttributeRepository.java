package cool.houge.mahu.admin.mart.repository;

import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.mart.ProductAttribute;
import io.ebean.Database;
import jakarta.inject.Singleton;

/// 产品属性
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ProductAttributeRepository extends HBeanRepository<Long, ProductAttribute> {

    public ProductAttributeRepository(Database db) {
        super(ProductAttribute.class, db);
    }
}

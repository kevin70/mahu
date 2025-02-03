package cool.houge.mahu.admin.mart.repository;

import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.mart.Product;
import io.ebean.Database;
import jakarta.inject.Singleton;

/// 产品
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ProductRepository extends HBeanRepository<Long, Product> {

    public ProductRepository(Database db) {
        super(Product.class, db);
    }
}

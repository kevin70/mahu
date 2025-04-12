package cool.houge.mahu.admin.mart.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.FilterField;
import cool.houge.mahu.entity.mart.Product;
import cool.houge.mahu.entity.mart.ProductStatus;
import cool.houge.mahu.entity.mart.query.QProduct;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

import java.util.List;

/// 产品
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ProductRepository extends HBeanRepository<Long, Product> {

    public ProductRepository(Database db) {
        super(Product.class, db);
    }

    public PagedList<Product> findPage(DataFilter dataFilter) {
        var qb = new QProduct(db());
        var filterFields = List.of(
                FF_CREATED_AT,
                FF_UPDATED_AT,
                FilterField.with(qb.name).build(),
                FilterField.with(qb.status, ProductStatus::valueOf).build(),
                FilterField.with(qb.brand.id).filterName("brand_id").build()
                //
                );

        super.apply(dataFilter, filterFields, qb.query());
        return qb.findPagedList();
    }
}

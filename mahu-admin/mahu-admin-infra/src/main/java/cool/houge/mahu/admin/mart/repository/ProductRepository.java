package cool.houge.mahu.admin.mart.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.RSQLContext;
import cool.houge.mahu.entity.mart.Product;
import cool.houge.mahu.entity.mart.query.QProduct;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

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
        var rsqlCtx = RSQLContext.of(qb).property(qb.name);
        apply(dataFilter, rsqlCtx);
        return qb.findPagedList();
    }
}

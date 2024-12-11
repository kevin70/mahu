package cool.houge.mahu.admin.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.Brand;
import cool.houge.mahu.entity.query.QBrand;
import io.ebean.Database;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Singleton;

/// 品牌
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class BrandRepository extends HBeanRepository<Integer, Brand> {

    public BrandRepository(Database db) {
        super(Brand.class, db);
    }

    @Transactional(readOnly = true)
    public PagedList<Brand> findPage(DataFilter dataFilter) {
        var qb = new QBrand(db());
        return findPagedList(qb.query(), dataFilter);
    }
}

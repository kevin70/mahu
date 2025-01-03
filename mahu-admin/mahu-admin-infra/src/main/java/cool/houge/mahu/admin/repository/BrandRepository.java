package cool.houge.mahu.admin.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.RSQLContext;
import cool.houge.mahu.entity.Brand;
import cool.houge.mahu.entity.query.QBrand;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

/// 品牌
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class BrandRepository extends HBeanRepository<Integer, Brand> {

    public BrandRepository(Database db) {
        super(Brand.class, db);
    }

    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | create_time | date-time |
    /// | update_time | date-time |
    /// | name | string |
    /// | ordering | int |
    public PagedList<Brand> findPage(DataFilter dataFilter) {
        var qb = new QBrand(db());
        var rsqlCtx = RSQLContext.of(qb)
                .property("create_time", qb.createTime)
                .property("update_time", qb.updateTime)
                .property(qb.name)
                .property(qb.ordering);
        super.apply(dataFilter, rsqlCtx);
        return qb.findPagedList();
    }
}

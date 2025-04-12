package cool.houge.mahu.admin.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.FilterField;
import cool.houge.mahu.entity.Brand;
import cool.houge.mahu.entity.query.QBrand;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

import java.util.List;

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
    /// | created_at | date-time |
    /// | updated_at | date-time |
    /// | name | string |
    /// | ordering | int |
    public PagedList<Brand> findPage(DataFilter dataFilter) {
        var qb = new QBrand(db());
        var filterFields = List.of(
                FF_CREATED_AT,
                FF_UPDATED_AT,
                FilterField.with(qb.name).build(),
                FilterField.with(qb.ordering).build());

        super.apply(dataFilter, filterFields, qb.query());
        return qb.findPagedList();
    }
}

package cool.houge.mahu.admin.mart.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.FilterField;
import cool.houge.mahu.entity.mart.Shop;
import cool.houge.mahu.entity.mart.query.QShop;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;
import java.util.List;

/// 商店
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ShopRepository extends HBeanRepository<Integer, Shop> {

    public ShopRepository(Database db) {
        super(Shop.class, db);
    }

    /// 分页查询商店数据
    ///
    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | name | string |
    public PagedList<Shop> findPage(DataFilter dataFilter) {
        var qb = new QShop(db());
        var filterFields = List.of(
                FF_CREATED_AT,
                FF_UPDATED_AT,
                FilterField.builder().with(qb.name).build()
                //
                );

        super.apply(dataFilter, filterFields, qb);
        return qb.findPagedList();
    }
}

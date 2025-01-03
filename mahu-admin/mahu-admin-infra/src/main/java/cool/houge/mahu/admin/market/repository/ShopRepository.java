package cool.houge.mahu.admin.market.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.RSQLContext;
import cool.houge.mahu.entity.market.Shop;
import cool.houge.mahu.entity.market.query.QShop;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

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

        super.apply(dataFilter, RSQLContext.of(qb).property(qb.name));
        return qb.findPagedList();
    }
}

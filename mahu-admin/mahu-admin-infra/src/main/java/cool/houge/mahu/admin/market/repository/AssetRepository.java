package cool.houge.mahu.admin.market.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.market.Asset;
import cool.houge.mahu.entity.market.query.QAsset;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

import java.util.List;

/// 商店资源
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AssetRepository extends HBeanRepository<Long, Asset> {

    public AssetRepository(Database db) {
        super(Asset.class, db);
    }

    /// 分页查询商店资源数据
    public PagedList<Asset> findPage(int shopId, DataFilter dataFilter) {
        var qb = new QAsset(db());
        qb.shop.id.eq(shopId);
        return findPagedList(qb.query(), dataFilter);
    }

    /// 查询指定资源ID的商店资源
    ///
    /// @param shopId 商店 ID
    /// @param ids 资源 IDs
    public List<Asset> findByShopId$Ids(int shopId, List<Long> ids) {
        return new QAsset(db()).id.in(ids).shop.id.eq(shopId).findList();
    }
}

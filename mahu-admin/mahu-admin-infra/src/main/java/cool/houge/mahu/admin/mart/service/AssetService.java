package cool.houge.mahu.admin.mart.service;

import cool.houge.mahu.admin.mart.repository.AssetRepository;
import cool.houge.mahu.admin.shared.SharedToolService;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.mart.Asset;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

/// 商店资源
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AssetService {

    @Inject
    AssetRepository assetRepository;

    @Inject
    SharedToolService toolService;

    /// 批量保存
    @Transactional
    public void saveAll(List<Asset> assets) {
        assetRepository.saveAll(assets);
    }

    /// 分页查询商店资源数据
    /// @param shopId 商店 ID
    @Transactional(readOnly = true)
    public PagedList<Asset> findPage(int shopId, DataFilter dataFilter) {
        var plist = assetRepository.findPage(shopId, dataFilter);
        return toolService.wrap(plist, dataFilter);
    }

    /// 查询指定资源ID的商店资源
    ///
    /// @param shopId 商店 ID
    /// @param assetIds 资源 IDs
    @Transactional
    public void deleteShopAsset(int shopId, List<Long> assetIds) {
        var beans = assetRepository.findByShopId$Ids(shopId, assetIds);
        assetRepository.deleteAll(beans);
    }
}

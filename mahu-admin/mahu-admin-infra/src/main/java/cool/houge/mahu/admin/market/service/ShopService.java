package cool.houge.mahu.admin.market.service;

import cool.houge.mahu.admin.market.repository.ShopRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.market.Shop;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 商店
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ShopService {

    @Inject
    ShopRepository shopRepository;

    @Transactional
    public void save(Shop entity) {
        shopRepository.save(entity);
    }

    @Transactional
    public void update(Shop entity) {
        shopRepository.update(entity);
    }

    @Transactional
    public void deleteById(Integer id) {
        shopRepository.delete(new Shop().setId(id));
    }

    /// 查询指定 ID 的商店
    @Transactional(readOnly = true)
    public Shop findById(Integer id) {
        return shopRepository.findById(id);
    }

    /// 分页查询商店数据
    @Transactional(readOnly = true)
    public PagedList<Shop> findPage(DataFilter dataFilter) {
        return shopRepository.findPage(dataFilter);
    }
}

package cool.houge.mahu.admin.service;

import cool.houge.mahu.admin.repository.BrandRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.Brand;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 品牌
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class BrandService {

    @Inject
    BrandRepository brandRepository;

    @Transactional
    public void save(Brand brand) {
        brandRepository.save(brand);
    }

    @Transactional
    public void update(Brand brand) {
        brandRepository.update(brand);
    }

    @Transactional
    public void deleteById(Integer id) {
        brandRepository.delete(new Brand().setId(id));
    }

    @Transactional(readOnly = true)
    public Brand findById(Integer id) {
        return brandRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public PagedList<Brand> findPage(DataFilter dataFilter) {
        return brandRepository.findPage(dataFilter);
    }
}

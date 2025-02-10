package cool.houge.mahu.admin.mart.service;

import cool.houge.mahu.admin.mart.repository.ProductRepository;
import cool.houge.mahu.admin.mart.repository.ProductVariantRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.mart.Product;
import cool.houge.mahu.entity.mart.ProductStatus;
import cool.houge.mahu.entity.mart.ProductVariant;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 产品
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ProductService {

    @Inject
    ProductRepository productRepository;

    @Inject
    ProductVariantRepository productVariantRepository;

    /// 保存产品
    @Transactional
    public void save(Product product) {
        product.setStatus(ProductStatus.DRAFT);

        for (ProductVariant variant : product.getVariants()) {
            variant.setStatus(ProductStatus.DRAFT);
        }
        productRepository.save(product);
    }

    /// 更新产品
    @Transactional
    public void update(Product product) {
        productRepository.update(product);
    }

    /// 查询指定 ID 的产品
    @Transactional(readOnly = true)
    public Product findById(long id) {
        return productRepository.findById(id);
    }

    /// 分页查询
    @Transactional(readOnly = true)
    public PagedList<Product> findPage(DataFilter dataFilter) {
        return productRepository.findPage(dataFilter);
    }
}

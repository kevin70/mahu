package cool.houge.mahu.admin.mart.service;

import com.google.common.base.Joiner;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.mart.repository.ProductAttributeRepository;
import cool.houge.mahu.admin.mart.repository.ProductRepository;
import cool.houge.mahu.admin.mart.repository.ProductVariantAttributeRepository;
import cool.houge.mahu.admin.mart.repository.ProductVariantRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.mart.*;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Optional.ofNullable;

/// 产品
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantAttributeRepository productVariantAttributeRepository;

    @Inject
    public ProductService(
            ProductRepository productRepository,
            ProductAttributeRepository productAttributeRepository,
            ProductVariantRepository productVariantRepository,
            ProductVariantAttributeRepository productVariantAttributeRepository) {
        this.productRepository = productRepository;
        this.productAttributeRepository = productAttributeRepository;
        this.productVariantRepository = productVariantRepository;
        this.productVariantAttributeRepository = productVariantAttributeRepository;
    }

    /// 保存产品
    @Transactional
    public void save(Product product) {
        product.setStatus(ProductStatus.DRAFT);

        this.preProductVariant(product);
        productRepository.save(product);
    }

    /// 更新产品
    @Transactional
    public void update(Product product) {
        var dbEntity = productRepository.findById(product.getId());
        if (dbEntity == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND);
        }

        this.preProductVariant(product);

        // 删除的产品属性
        var attributeIds = product.getAttributes().stream()
                .map(ProductAttribute::getId)
                .filter(Objects::nonNull)
                .toList();
        productAttributeRepository.deleteByProductId$NotInIds(product.getId(), attributeIds);

        // 删除变体
        var variantIds = product.getVariants().stream()
                .map(ProductVariant::getId)
                .filter(Objects::nonNull)
                .toList();
        productVariantRepository.deleteByProductId$NotInIds(product.getId(), variantIds);

        // 删除变体属性
        var variantAttributeIds = new ArrayList<Integer>();
        for (ProductVariant variant : product.getVariants()) {
            for (ProductVariantAttribute attribute : variant.getAttributes()) {
                if (!variantAttributeIds.contains(attribute.getAttribute().getId())) {
                    variantAttributeIds.add(attribute.getAttribute().getId());
                }
            }
        }
        productVariantAttributeRepository.deleteByProductId$NotInAttributeIds(product.getId(), variantAttributeIds);

        // 更新产品
        productRepository.update(product);
    }

    /// 更新产品状态
    @Transactional
    public void updateStatus(Product bean) {
        if (bean.getStatus() != null) {
            productRepository.update(bean);
        }

        if (bean.getVariants() != null && !bean.getVariants().isEmpty()) {
            // FIXME 使用产品ID + 变体ID更新状态
            productVariantRepository.saveAll(bean.getVariants());
        }
    }

    /// 删除指定的产品
    @Transactional
    public void delete(long productId) {
        productRepository.delete(new Product().setId(productId));
    }

    /// 查询指定 ID 的产品
    @Transactional(readOnly = true)
    public Product findById(long id) {
        var product = productRepository.findById(id);
        if (product == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, "未找到指定的产品[" + id + "]");
        }
        return product;
    }

    /// 分页查询
    @Transactional(readOnly = true)
    public PagedList<Product> findPage(DataFilter dataFilter) {
        return productRepository.findPage(dataFilter);
    }

    void preProductVariant(Product product) {
        for (ProductVariant variant : product.getVariants()) {
            if (variant.getStatus() == null) {
                variant.setStatus(ProductStatus.DRAFT);
            }

            var attributeValues = new ArrayList<String>();

            // 变体属性与产品关联
            var attributes = ofNullable(variant.getAttributes()).orElseGet(List::of);
            for (ProductVariantAttribute o : attributes) {
                o.setProduct(product);
                attributeValues.add(o.getValue());
            }

            // 变体限定名
            variant.setQn(Joiner.on('|').join(attributeValues));
        }
    }
}

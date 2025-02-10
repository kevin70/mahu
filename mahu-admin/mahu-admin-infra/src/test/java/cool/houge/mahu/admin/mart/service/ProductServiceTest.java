package cool.houge.mahu.admin.mart.service;

import cool.houge.mahu.admin.TestTransactionBase;
import cool.houge.mahu.entity.mart.AttributeValue;
import cool.houge.mahu.entity.mart.Product;
import cool.houge.mahu.entity.mart.ProductAttribute;
import io.ebean.annotation.SoftDelete;
import jakarta.inject.Inject;
import jakarta.persistence.Id;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import static org.instancio.Select.fields;

/// 产品
///
/// @author ZY (kzou227@qq.com)
class ProductServiceTest extends TestTransactionBase {

    @Inject
    ProductService productService;

    @Test
    void save() {
        var entity = Instancio.of(Product.class)
                .ignore(fields().annotated(Id.class))
                .ignore(fields().annotated(SoftDelete.class))
                .create();
        productService.save(entity);
    }

    @Test
    void update() {
        var entity = productService.findById(4);
        entity.setName("UPDATE product");

        var attributes = entity.getAttributes();
        for (ProductAttribute attribute : attributes) {
            attribute.setValue("NEW " + attribute.getValue()).setAttributeValue(new AttributeValue().setId(-1));
        }
        var newAttribute = Instancio.of(ProductAttribute.class)
                .ignore(fields().annotated(Id.class))
                .ignore(fields().annotated(SoftDelete.class))
                .create();
        attributes.add(newAttribute);

        productService.update(entity);
    }
}

package cool.houge.mahu.admin.mart.service;

import cool.houge.mahu.admin.TestBase;
import cool.houge.mahu.entity.mart.Product;
import io.ebean.annotation.SoftDelete;
import jakarta.inject.Inject;
import jakarta.persistence.Id;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.fields;

/// 产品
///
/// @author ZY (kzou227@qq.com)
class ProductServiceTest extends TestBase {

    @Inject
    ProductService productService;

    @Test
    void save() {
        var entity = Instancio.of(Product.class)
                .ignore(fields().annotated(Id.class))
                .ignore(fields().annotated(SoftDelete.class))
                .create();
        productService.save(entity);
        assertThat(entity.getId()).isNotNull();
    }
}

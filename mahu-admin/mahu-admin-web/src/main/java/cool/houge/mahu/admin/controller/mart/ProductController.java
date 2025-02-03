package cool.houge.mahu.admin.controller.mart;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.mart.service.ProductService;
import cool.houge.mahu.admin.oas.model.UpsertMartProductRequest;
import cool.houge.mahu.common.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static cool.houge.mahu.admin.Permits.MART_PRODUCT;
import static io.helidon.http.Status.NO_CONTENT_204;

/// 产品
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ProductController implements WebSupport, HttpService {

    @Inject
    VoBeanMapper beanMapper;

    @Inject
    ProductService productService;

    @Override
    public void routing(HttpRules rules) {
        rules.post("/mart/products", authz(MART_PRODUCT.W()).wrap(this::addMartProduct));
        rules.put("/mart/products/{product_id}", authz(MART_PRODUCT.W()).wrap(this::updateMartProduct));
    }

    private void addMartProduct(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertMartProductRequest.class);
        validate(vo);

        var entity = beanMapper.toProduct(vo);
        productService.save(entity);
        response.status(NO_CONTENT_204).send();
    }

    private void updateMartProduct(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var productId = pathParams.first("product_id").asLong().get();

        var vo = request.content().as(UpsertMartProductRequest.class);
        validate(vo);

        var entity = beanMapper.toProduct(vo).setId(productId);
        productService.save(entity);
        response.status(NO_CONTENT_204).send();
    }
}

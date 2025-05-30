package cool.houge.mahu.admin.controller.mart;

import static cool.houge.mahu.admin.Permits.MART_PRODUCT;
import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.mart.service.ProductService;
import cool.houge.mahu.admin.oas.model.UpdateMartProductStatusRequest;
import cool.houge.mahu.admin.oas.model.UpsertMartProductRequest;
import cool.houge.mahu.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 产品
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ProductController implements WebSupport, HttpService {

    private final VoBeanMapper beanMapper;
    private final ProductService productService;

    @Inject
    public ProductController(VoBeanMapper beanMapper, ProductService productService) {
        this.beanMapper = beanMapper;
        this.productService = productService;
    }

    @SuppressWarnings("java:S1192")
    @Override
    public void routing(HttpRules rules) {
        rules.get("/mart/products", s(this::listMartProducts, MART_PRODUCT.R));
        rules.get("/mart/products/{product_id}", s(this::getMartProduct, MART_PRODUCT.R));

        rules.post("/mart/products", s(this::createMartProduct, MART_PRODUCT.W));
        rules.put("/mart/products/{product_id}", s(this::updateMartProduct, MART_PRODUCT.W));
        rules.delete("/mart/products/{product_id}", s(this::deleteMartProduct, MART_PRODUCT.D));

        rules.put("/mart/products/{product_id}/status", s(this::updateMartProductStatus, MART_PRODUCT.W));
    }

    private void listMartProducts(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = productService.findPage(dataFilter);
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toMartProductResponse);
        response.send(rs);
    }

    private void getMartProduct(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var productId = pathParams.first("product_id").asLong().get();

        var bean = productService.findById(productId);
        var rs = beanMapper.toMartProductResponse(bean);
        response.send(rs);
    }

    private void createMartProduct(ServerRequest request, ServerResponse response) {
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
        productService.update(entity);
        response.status(NO_CONTENT_204).send();
    }

    private void deleteMartProduct(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var productId = pathParams.first("product_id").asLong().get();

        productService.delete(productId);
        response.status(NO_CONTENT_204).send();
    }

    private void updateMartProductStatus(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var productId = pathParams.first("product_id").asLong().get();

        var vo = request.content().as(UpdateMartProductStatusRequest.class);
        validate(vo);

        var bean = beanMapper.toProduct(vo).setId(productId);
        productService.updateStatus(bean);
        response.status(NO_CONTENT_204).send();
    }
}

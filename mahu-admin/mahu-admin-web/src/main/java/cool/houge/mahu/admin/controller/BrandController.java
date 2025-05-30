package cool.houge.mahu.admin.controller;

import static cool.houge.mahu.admin.Permits.BRAND;
import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.UpsertBrandRequest;
import cool.houge.mahu.admin.service.BrandService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 品牌
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class BrandController implements HttpService, WebSupport {

    private final VoBeanMapper beanMapper;
    private final BrandService brandService;

    @Inject
    public BrandController(VoBeanMapper beanMapper, BrandService brandService) {
        this.beanMapper = beanMapper;
        this.brandService = brandService;
    }

    @SuppressWarnings("java:S1192")
    @Override
    public void routing(HttpRules rules) {
        rules.get("/brands", s(this::listBrands, BRAND.R));
        rules.post("/brands", s(this::addBrand, BRAND.W));
        rules.get("/brands/{id:\\d+}", s(this::getBrand, BRAND.R));
        rules.put("/brands/{id:\\d+}", s(this::updateBrand, BRAND.W));
        rules.delete("/brands/{id:\\d+}", s(this::deleteBrand, BRAND.D));
    }

    private void listBrands(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);

        var plist = brandService.findPage(dataFilter);
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toBrandResponse);
        response.send(rs);
    }

    private void getBrand(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        var bean = brandService.findById(id);
        var rs = beanMapper.toBrandResponse(bean);
        response.send(rs);
    }

    private void addBrand(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertBrandRequest.class);
        validate(vo);

        var entity = beanMapper.toBrand(vo);
        brandService.save(entity);
        response.status(NO_CONTENT_204).send();
    }

    private void updateBrand(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertBrandRequest.class);
        validate(vo);

        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        var entity = beanMapper.toBrand(vo).setId(id);
        brandService.update(entity);
        response.status(NO_CONTENT_204).send();
    }

    private void deleteBrand(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        brandService.deleteById(id);
        response.status(NO_CONTENT_204).send();
    }
}

package cool.houge.mahu.admin.controller;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.UpsertBrandRequest;
import cool.houge.mahu.admin.service.BrandService;
import cool.houge.mahu.common.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static cool.houge.mahu.admin.Permits.BRAND;

/// 品牌
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class BrandController implements HttpService, WebSupport {

    @Inject
    BrandService brandService;

    @Inject
    VoBeanMapper beanMapper;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/brands", authz(BRAND.R()).wrap(this::listBrands));
        rules.post("/brands", authz(BRAND.W()).wrap(this::addBrand));
        rules.get("/brands/{id:\\d+}", authz(BRAND.R()).wrap(this::getBrand));
        rules.put("/brands/{id:\\d+}", authz(BRAND.W()).wrap(this::updateBrand));
        rules.delete("/brands/{id:\\d+}", authz(BRAND.D()).wrap(this::deleteBrand));
    }

    private void listBrands(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);

        var plist = brandService.findPage(dataFilter);
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toGetBrandResponse);
        response.send(rs);
    }

    private void getBrand(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();
    }

    private void addBrand(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertBrandRequest.class);
        validate(vo);

        var entity = beanMapper.toBrand(vo);
    }

    private void updateBrand(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertBrandRequest.class);
        validate(vo);

        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        var entity = beanMapper.toBrand(vo).setId(id);
    }

    private void deleteBrand(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();
    }
}

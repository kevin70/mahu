package cool.houge.mahu.admin.controller.market;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.market.service.ShopService;
import cool.houge.mahu.admin.oas.model.UpsertShopRequest;
import cool.houge.mahu.common.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static cool.houge.mahu.admin.Permits.MARKET_SHOP;
import static io.helidon.http.Status.NO_CONTENT_204;

/// 商店
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ShopController implements HttpService, WebSupport {

    @Inject
    ShopService shopService;

    @Inject
    VoBeanMapper beanMapper;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/shops", authz(MARKET_SHOP.R()).wrap(this::listShops));
        rules.get("/shops/{id:\\d+}", authz(MARKET_SHOP.R()).wrap(this::getShop));

        rules.post("/shops", authz(MARKET_SHOP.W()).wrap(this::addShop));
        rules.put("/shops/{id:\\d+}", authz(MARKET_SHOP.W()).wrap(this::updateShop));
        rules.delete("/shops/{id:\\d+}", authz(MARKET_SHOP.W()).wrap(this::deleteShop));
    }

    private void listShops(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = shopService.findPage(dataFilter);
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toGetShopResponse);
        response.send(rs);
    }

    private void getShop(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        var bean = shopService.findById(id);
        var rs = beanMapper.toGetShopResponse(bean);
        response.send(rs);
    }

    private void addShop(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertShopRequest.class);
        validate(vo);

        var entity = beanMapper.toShop(vo);
        shopService.save(entity);
        response.status(NO_CONTENT_204).send();
    }

    private void updateShop(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertShopRequest.class);
        validate(vo);

        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        var entity = beanMapper.toShop(vo).setId(id);
        shopService.update(entity);
        response.status(NO_CONTENT_204).send();
    }

    private void deleteShop(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        shopService.deleteById(id);
        response.status(NO_CONTENT_204).send();
    }
}

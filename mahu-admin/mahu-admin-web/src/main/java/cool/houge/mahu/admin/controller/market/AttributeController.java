package cool.houge.mahu.admin.controller.market;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.market.service.AttributeService;
import cool.houge.mahu.admin.oas.model.UpsertMarketAttributeRequest;
import cool.houge.mahu.common.web.WebSupport;
import cool.houge.mahu.entity.market.Attribute;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static cool.houge.mahu.admin.Permits.MARKET_ATTRIBUTE;
import static io.helidon.http.Status.NO_CONTENT_204;

/// 商品属性
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AttributeController implements HttpService, WebSupport {

    @Inject
    VoBeanMapper beanMapper;

    @Inject
    AttributeService attributeService;

    @Override
    public void routing(HttpRules rules) {
        rules.post("/market/attributes", authz(MARKET_ATTRIBUTE.W()).wrap(this::addMarketAttribute));
        rules.delete("/market/attributes/{id}", authz(MARKET_ATTRIBUTE.W()).wrap(this::deleteMarketAttribute));
        rules.put("/market/attributes/{id}", authz(MARKET_ATTRIBUTE.W()).wrap(this::updateMarketAttribute));

        rules.get("/market/attributes/{id}", authz(MARKET_ATTRIBUTE.R()).wrap(this::getMarketAttribute));
        rules.get("/market/attributes", authz(MARKET_ATTRIBUTE.R()).wrap(this::listMarketAttributes));
    }

    private void addMarketAttribute(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertMarketAttributeRequest.class);
        validate(vo);

        var entity = beanMapper.toAttribute(vo);
        attributeService.save(entity);
        response.status(NO_CONTENT_204).send();
    }

    private void deleteMarketAttribute(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        attributeService.delete(new Attribute().setId(id));
        response.status(NO_CONTENT_204).send();
    }

    private void updateMarketAttribute(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();
        var vo = request.content().as(UpsertMarketAttributeRequest.class);
        validate(vo);

        var entity = beanMapper.toAttribute(vo).setId(id);
        attributeService.update(entity);
        response.status(NO_CONTENT_204).send();
    }

    private void getMarketAttribute(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        var bean = attributeService.findById(id);
        var rs = beanMapper.toGetMarketAttributeResponse(bean);
        response.send(rs);
    }

    private void listMarketAttributes(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);

        var plist = attributeService.findPage(dataFilter);
        var rs = beanMapper.toPageResponse(
                plist.getList(), plist.getTotalCount(), beanMapper::toGetMarketAttributeResponse);
        response.send(rs);
    }
}

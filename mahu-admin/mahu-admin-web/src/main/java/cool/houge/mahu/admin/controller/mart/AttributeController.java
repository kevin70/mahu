package cool.houge.mahu.admin.controller.mart;

import static cool.houge.mahu.admin.Permits.MART_ATTRIBUTE;
import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.mart.service.AttributeService;
import cool.houge.mahu.admin.oas.model.UpsertMartAttributeRequest;
import cool.houge.mahu.admin.oas.model.UpsertMartAttributeValueRequest;
import cool.houge.mahu.entity.mart.Attribute;
import cool.houge.mahu.entity.mart.AttributeValue;
import cool.houge.mahu.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 商品属性
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AttributeController implements HttpService, WebSupport {

    private final VoBeanMapper beanMapper;
    private final AttributeService attributeService;

    @Inject
    public AttributeController(VoBeanMapper beanMapper, AttributeService attributeService) {
        this.beanMapper = beanMapper;
        this.attributeService = attributeService;
    }

    @SuppressWarnings("java:S1192")
    @Override
    public void routing(HttpRules rules) {
        rules.post("/mart/attributes", s(this::createMartAttribute, MART_ATTRIBUTE.W));
        rules.delete("/mart/attributes/{id}", s(this::deleteMartAttribute, MART_ATTRIBUTE.W));
        rules.put("/mart/attributes/{id}", s(this::updateMartAttribute, MART_ATTRIBUTE.W));

        rules.get("/mart/attributes/{id}", s(this::getMartAttribute, MART_ATTRIBUTE.R));
        rules.get("/mart/attributes", s(this::listMartAttributes, MART_ATTRIBUTE.R));

        rules.post("/mart/attributes/{attribute_id}/values", s(this::addMartAttributeValue, MART_ATTRIBUTE.W));

        var values = "/mart/attributes/{attribute_id}/values/{attribute_value_id}";
        rules.delete(values, s(this::deleteMartAttributeValue, MART_ATTRIBUTE.D));
        rules.put(values, s(this::updateMartAttributeValue, MART_ATTRIBUTE.D));
        rules.get(values, s(this::getMartAttributeValue, MART_ATTRIBUTE.R));
    }

    private void createMartAttribute(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertMartAttributeRequest.class);
        validate(vo);

        var entity = beanMapper.toAttribute(vo);
        attributeService.save(entity);
        response.status(NO_CONTENT_204).send();
    }

    private void deleteMartAttribute(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        attributeService.delete(new Attribute().setId(id));
        response.status(NO_CONTENT_204).send();
    }

    private void updateMartAttribute(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();
        var vo = request.content().as(UpsertMartAttributeRequest.class);
        validate(vo);

        var entity = beanMapper.toAttribute(vo).setId(id);
        attributeService.update(entity);
        response.status(NO_CONTENT_204).send();
    }

    private void getMartAttribute(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        var bean = attributeService.findById(id);
        var rs = beanMapper.toMartAttributeResponse(bean);
        response.send(rs);
    }

    private void listMartAttributes(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);

        var plist = attributeService.findPage(dataFilter);
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toMartAttributeResponse);
        response.send(rs);
    }

    // ====================== 商品属性值 ====================== //
    private void addMartAttributeValue(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var attributeId = pathParams.first("attribute_id").asInt().get();
        var vo = request.content().as(UpsertMartAttributeValueRequest.class);
        validate(vo);

        var entity = beanMapper.toAttributeValue(attributeId, vo);
        attributeService.save(entity);
        response.status(NO_CONTENT_204).send();
    }

    private void deleteMartAttributeValue(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var attributeValueId = pathParams.first("attribute_value_id").asInt().get();

        attributeService.delete(new AttributeValue().setId(attributeValueId));
        response.status(NO_CONTENT_204).send();
    }

    private void updateMartAttributeValue(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var attributeId = pathParams.first("attribute_id").asInt().get();
        var attributeValueId = pathParams.first("attribute_value_id").asInt().get();
        var vo = request.content().as(UpsertMartAttributeValueRequest.class);
        validate(vo);

        var entity = beanMapper.toAttributeValue(attributeId, vo).setId(attributeValueId);
        attributeService.update(entity);
        response.status(NO_CONTENT_204).send();
    }

    private void getMartAttributeValue(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var attributeValueId = pathParams.first("attribute_value_id").asInt().get();

        var bean = attributeService.findValueById(attributeValueId);
        var rs = beanMapper.toMartAttributeValueResponse(bean);
        response.send(rs);
    }
}

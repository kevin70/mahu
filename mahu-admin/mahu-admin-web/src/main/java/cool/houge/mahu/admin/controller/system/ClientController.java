package cool.houge.mahu.admin.controller.system;

import static cool.houge.mahu.admin.Permissions.CLIENT;
import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.UpsertClientRequest;
import cool.houge.mahu.admin.system.service.ClientService;
import cool.houge.mahu.entity.AuthClient;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// 认证客户端
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class ClientController implements WebSupport {

    private final VoBeanMapper beanMapper;
    private final ClientService clientService;

    @SuppressWarnings("java:S1192")
    @Override
    public void routing(HttpRules rules) {
        rules.get("/system/clients", s(this::pageSystemClients, CLIENT.R));
        rules.get("/system/clients/{client_id}", s(this::getSystemClient, CLIENT.R));
        rules.post("/system/clients", s(this::createSystemClient, CLIENT.W));
        rules.put("/system/clients/{client_id}", s(this::updateSystemClient, CLIENT.W));
        rules.delete("/system/clients/{client_id}", s(this::deleteSystemClient, CLIENT.W));
    }

    void pageSystemClients(ServerRequest request, ServerResponse response) {
        var filter = dataFilter(request);
        var plist = clientService.findPage(filter);
        var rs = filter.toResult(plist, beanMapper::toClientResponse);
        response.send(rs);
    }

    void createSystemClient(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertClientRequest.class);
        validate(vo);

        var entity = beanMapper.toClient(vo);
        clientService.save(entity);
        response.status(NO_CONTENT_204).send();
    }

    void updateSystemClient(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertClientRequest.class);
        validate(vo);

        var pathParams = request.path().pathParameters();
        var clientId = pathParams.first("client_id").get();

        var entity = beanMapper.toClient(vo).setClientId(clientId);
        clientService.update(entity);
        response.status(NO_CONTENT_204).send();
    }

    void deleteSystemClient(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var clientId = pathParams.first("client_id").get();

        clientService.delete(new AuthClient().setClientId(clientId));
        response.status(NO_CONTENT_204).send();
    }

    void getSystemClient(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var clientId = pathParams.first("client_id").get();

        var bean = clientService.findById(clientId);
        response.send(beanMapper.toClientResponse(bean));
    }
}

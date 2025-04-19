package cool.houge.mahu.admin.controller.system;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.UpsertClientRequest;
import cool.houge.mahu.admin.system.service.ClientService;
import cool.houge.mahu.common.web.WebSupport;
import cool.houge.mahu.entity.system.Client;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static cool.houge.mahu.admin.Permits.CLIENT;
import static io.helidon.http.Status.NO_CONTENT_204;

/// 认证客户端
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class ClientController implements HttpService, WebSupport {

    @Inject
    VoBeanMapper beanMapper;

    @Inject
    ClientService clientService;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/system/clients", authz(CLIENT.R()).wrap(this::listClients));
        rules.get("/system/clients/{client_id}", authz(CLIENT.R()).wrap(this::getClient));
        rules.post("/system/clients", authz(CLIENT.W()).wrap(this::createClient));
        rules.put("/system/clients/{client_id}", authz(CLIENT.W()).wrap(this::updateClient));
        rules.delete("/system/clients/{client_id}", authz(CLIENT.W()).wrap(this::deleteClient));
    }

    void listClients(ServerRequest request, ServerResponse response) {
        var filter = dataFilter(request);
        var plist = clientService.findPage(filter);
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toClientResponse);
        response.send(rs);
    }

    void createClient(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertClientRequest.class);
        validate(vo);

        var entity = beanMapper.toClient(vo);
        clientService.save(entity);
        response.status(NO_CONTENT_204).send();
    }

    void updateClient(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertClientRequest.class);
        validate(vo);

        var pathParams = request.path().pathParameters();
        var clientId = pathParams.first("client_id").get();

        var entity = beanMapper.toClient(vo).setClientId(clientId);
        clientService.update(entity);
        response.status(NO_CONTENT_204).send();
    }

    void deleteClient(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var clientId = pathParams.first("client_id").get();

        clientService.delete(new Client().setClientId(clientId));
        response.status(NO_CONTENT_204).send();
    }

    void getClient(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var clientId = pathParams.first("client_id").get();

        var bean = clientService.findById(clientId);
        response.send(beanMapper.toClientResponse(bean));
    }
}

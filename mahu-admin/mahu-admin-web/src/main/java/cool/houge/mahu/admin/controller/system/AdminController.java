package cool.houge.mahu.admin.controller.system;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.UpsertAdminRequest;
import cool.houge.mahu.admin.system.service.AdminService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static cool.houge.mahu.admin.Permits.ADMIN;
import static io.helidon.http.Status.NO_CONTENT_204;

/// 职员
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AdminController implements HttpService, WebSupport {

    @Inject
    AdminService adminService;

    @Inject
    VoBeanMapper beanMapper;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/system/admins", authz(ADMIN.R()).wrap(this::listAdmins));
        rules.post("/system/admins", authz(ADMIN.W()).wrap(this::createAdmin));
        rules.put("/system/admins/{id:\\d+}", authz(ADMIN.W()).wrap(this::updateAdmin));
        rules.delete("/system/admins/{id:\\d+}", authz(ADMIN.W()).wrap(this::deleteAdmin));
        rules.get("/system/admins/{id:\\d+}", authz(ADMIN.R()).wrap(this::getAdmin));
    }

    private void listAdmins(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);

        var plist = adminService.findPage(dataFilter);
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toAdminResponse);
        response.send(rs);
    }

    private void createAdmin(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertAdminRequest.class);
        validate(vo);

        var entity = beanMapper.toAdmin(vo);
        adminService.save(entity);
        response.status(NO_CONTENT_204).send();
    }

    private void updateAdmin(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertAdminRequest.class);
        validate(vo);

        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asLong().get();

        var entity = beanMapper.toAdmin(vo).setId(id);
        adminService.update(entity);
        response.status(NO_CONTENT_204).send();
    }

    private void deleteAdmin(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asLong().get();

        adminService.delete(id);
        response.status(NO_CONTENT_204).send();
    }

    private void getAdmin(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asLong().get();

        var entity = adminService.obtainById(id);
        var rs = beanMapper.toAdminResponse(entity);
        response.send(rs);
    }
}

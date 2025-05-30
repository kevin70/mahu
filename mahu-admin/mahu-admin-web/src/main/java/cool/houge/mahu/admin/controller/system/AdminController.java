package cool.houge.mahu.admin.controller.system;

import static cool.houge.mahu.admin.Permits.ADMIN;
import static io.helidon.http.Status.NO_CONTENT_204;

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

/// 职员
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AdminController implements HttpService, WebSupport {

    private final VoBeanMapper beanMapper;
    private final AdminService adminService;

    @Inject
    public AdminController(VoBeanMapper beanMapper, AdminService adminService) {
        this.beanMapper = beanMapper;
        this.adminService = adminService;
    }

    @SuppressWarnings("java:S1192")
    @Override
    public void routing(HttpRules rules) {
        rules.get("/system/admins", s(this::listAdmins, ADMIN.R));
        rules.post("/system/admins", s(this::createAdmin, ADMIN.W));
        rules.put("/system/admins/{id:\\d+}", s(this::updateAdmin, ADMIN.W));
        rules.delete("/system/admins/{id:\\d+}", s(this::deleteAdmin, ADMIN.W));
        rules.get("/system/admins/{id:\\d+}", s(this::getAdmin, ADMIN.R));
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

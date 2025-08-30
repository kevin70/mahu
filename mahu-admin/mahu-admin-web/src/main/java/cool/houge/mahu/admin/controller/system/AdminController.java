package cool.houge.mahu.admin.controller.system;

import static cool.houge.mahu.admin.Permissions.ADMIN;
import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.UpsertAdminRequest;
import cool.houge.mahu.admin.system.service.AdminService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// 管理员
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class AdminController implements WebSupport {

    private final VoBeanMapper beanMapper;
    private final AdminService adminService;

    @SuppressWarnings("java:S1192")
    @Override
    public void routing(HttpRules rules) {
        rules.get("/system/admins", s(this::pageSystemAdmins, ADMIN.R));
        rules.post("/system/admins", s(this::createSystemAdmin, ADMIN.W));
        rules.put("/system/admins/{admin_id}", s(this::updateSystemAdmin, ADMIN.W));
        rules.delete("/system/admins/{admin_id}", s(this::deleteSystemAdmin, ADMIN.W));
        rules.get("/system/admins/{admin_id}", s(this::getSystemAdmin, ADMIN.R));
    }

    private void pageSystemAdmins(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);

        var plist = adminService.findPage(dataFilter);
        var rs = dataFilter.toResult(plist, beanMapper::toAdminResponse);
        response.send(rs);
    }

    private void createSystemAdmin(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertAdminRequest.class);
        validate(vo);

        var entity = beanMapper.toAdmin(vo);
        adminService.save(entity);
        response.status(NO_CONTENT_204).send();
    }

    private void updateSystemAdmin(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertAdminRequest.class);
        validate(vo);
        var id = adminId(request);

        var entity = beanMapper.toAdmin(vo).setId(id);
        adminService.update(entity);
        response.status(NO_CONTENT_204).send();
    }

    private void deleteSystemAdmin(ServerRequest request, ServerResponse response) {
        var id = adminId(request);
        adminService.delete(id);
        response.status(NO_CONTENT_204).send();
    }

    private void getSystemAdmin(ServerRequest request, ServerResponse response) {
        var id = adminId(request);
        var entity = adminService.obtainById(id);
        var rs = beanMapper.toAdminResponse(entity);
        response.send(rs);
    }

    long adminId(ServerRequest request) {
        return pathArg(request, "admin_id").asLong().get();
    }
}

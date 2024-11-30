package cool.houge.mahu.admin.controller.system;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.UpsertRoleRequest;
import cool.houge.mahu.admin.system.service.RoleService;
import cool.houge.mahu.common.web.WebDataFilter;
import cool.houge.mahu.common.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static cool.houge.mahu.admin.Permits.ROLE;
import static io.helidon.http.Status.NO_CONTENT_204;

/// 角色
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class RoleController implements HttpService, WebSupport {

    @Inject
    RoleService roleService;

    @Inject
    VoBeanMapper beanMapper;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/system/roles", authz(ROLE.R()).wrap(this::listRoles));
        rules.post("/system/roles", authz(ROLE.W()).wrap(this::addRole));
        rules.put("/system/roles/{id:\\d+}", authz(ROLE.W()).wrap(this::updateRole));
        rules.delete("/system/roles/{id:\\d+}", authz(ROLE.W()).wrap(this::deleteRole));
        rules.get("/system/roles/{id:\\d+}", authz(ROLE.R()).wrap(this::getRole));
    }

    private void addRole(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertRoleRequest.class);
        validate(vo);

        var bean = beanMapper.toRole(vo);
        roleService.save(bean);
        response.status(NO_CONTENT_204).send();
    }

    private void updateRole(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertRoleRequest.class);
        validate(vo);

        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        var bean = beanMapper.toRole(vo).setId(id);
        roleService.update(bean);
        response.status(NO_CONTENT_204).send();
    }

    private void deleteRole(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        roleService.deleteById(id);
        response.status(NO_CONTENT_204).send();
    }

    private void getRole(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        var bean = roleService.findById(id);
        var rs = beanMapper.toGetRoleResponse(bean);
        response.send(rs);
    }

    private void listRoles(ServerRequest request, ServerResponse response) {
        var plist = roleService.findPage(new WebDataFilter(request));
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toGetRoleResponse);
        response.send(rs);
    }
}

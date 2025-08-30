package cool.houge.mahu.admin.controller.system;

import static cool.houge.mahu.admin.Permissions.ROLE;
import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.UpsertRoleRequest;
import cool.houge.mahu.admin.system.service.RoleService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// 角色
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class RoleController implements WebSupport {

    private final VoBeanMapper beanMapper;
    private final RoleService roleService;

    @SuppressWarnings("java:S1192")
    @Override
    public void routing(HttpRules rules) {
        rules.get("/system/roles", s(this::pageSystemRoles, ROLE.R));
        rules.post("/system/roles", s(this::createSystemRole, ROLE.W));
        rules.put("/system/roles/{role_id}", s(this::updateSystemRole, ROLE.W));
        rules.delete("/system/roles/{role_id}", s(this::deleteSystemRole, ROLE.W));
        rules.get("/system/roles/{role_id}", s(this::getSystemRole, ROLE.R));
    }

    private void createSystemRole(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertRoleRequest.class);
        validate(vo);

        var bean = beanMapper.toRole(vo);
        roleService.save(bean);
        response.status(NO_CONTENT_204).send();
    }

    private void updateSystemRole(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpsertRoleRequest.class);
        validate(vo);

        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        var bean = beanMapper.toRole(vo).setId(id);
        roleService.update(bean);
        response.status(NO_CONTENT_204).send();
    }

    private void deleteSystemRole(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        roleService.deleteById(id);
        response.status(NO_CONTENT_204).send();
    }

    private void getSystemRole(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        var bean = roleService.findById(id);
        var rs = beanMapper.toRoleResponse(bean);
        response.send(rs);
    }

    private void pageSystemRoles(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = roleService.findPage(dataFilter);
        var rs = dataFilter.toResult(plist, beanMapper::toRoleResponse);
        response.send(rs);
    }
}

package cool.houge.mahu.admin.controller.system;

import static cool.houge.mahu.admin.Permits.ROLE;
import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.UpsertRoleRequest;
import cool.houge.mahu.admin.system.service.RoleService;
import cool.houge.mahu.web.WebDataFilter;
import cool.houge.mahu.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 角色
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class RoleController implements HttpService, WebSupport {

    private final VoBeanMapper beanMapper;
    private final RoleService roleService;

    @Inject
    public RoleController(VoBeanMapper beanMapper, RoleService roleService) {
        this.beanMapper = beanMapper;
        this.roleService = roleService;
    }

    @SuppressWarnings("java:S1192")
    @Override
    public void routing(HttpRules rules) {
        rules.get("/system/roles", s(this::listRoles, ROLE.R));
        rules.post("/system/roles", s(this::createRole, ROLE.W));
        rules.put("/system/roles/{id:\\d+}", s(this::updateRole, ROLE.W));
        rules.delete("/system/roles/{id:\\d+}", s(this::deleteRole, ROLE.W));
        rules.get("/system/roles/{id:\\d+}", s(this::getRole, ROLE.R));
    }

    private void createRole(ServerRequest request, ServerResponse response) {
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
        var rs = beanMapper.toRoleResponse(bean);
        response.send(rs);
    }

    private void listRoles(ServerRequest request, ServerResponse response) {
        var plist = roleService.findPage(new WebDataFilter(request));
        var rs = beanMapper.toPageResponse(plist.getList(), plist.getTotalCount(), beanMapper::toRoleResponse);
        response.send(rs);
    }
}

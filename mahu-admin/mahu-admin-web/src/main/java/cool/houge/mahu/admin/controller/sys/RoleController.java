package cool.houge.mahu.admin.controller.sys;

import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.oas.controller.HRoleService;
import cool.houge.mahu.admin.oas.vo.SysRoleUpsertRequest;
import cool.houge.mahu.admin.sys.service.RoleService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// 角色
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class RoleController implements HRoleService, WebSupport {

    private final SysBeanMapper beanMapper;
    private final RoleService roleService;

    @Override
    public void createSysRole(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(SysRoleUpsertRequest.class);
        validate(vo);

        var bean = beanMapper.toRole(vo);
        roleService.save(bean);
        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void deleteSysRole(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        roleService.deleteById(id);
        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void getSysRole(ServerRequest request, ServerResponse response) {
        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        var bean = roleService.findById(id);
        var rs = beanMapper.toSysRoleResponse(bean);
        response.send(rs);
    }

    @Override
    public void pageSysRole(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var plist = roleService.findPage(dataFilter);
        var rs = dataFilter.toResult(plist, beanMapper::toSysRoleResponse);
        response.send(rs);
    }

    @Override
    public void updateSysRole(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(SysRoleUpsertRequest.class);
        validate(vo);

        var pathParams = request.path().pathParameters();
        var id = pathParams.first("id").asInt().get();

        var bean = beanMapper.toRole(vo).setId(id);
        roleService.update(bean);
        response.status(NO_CONTENT_204).send();
    }
}

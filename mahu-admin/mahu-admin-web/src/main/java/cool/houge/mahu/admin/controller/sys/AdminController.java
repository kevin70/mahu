package cool.houge.mahu.admin.controller.sys;

import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.controller.HAdminService;
import cool.houge.mahu.admin.oas.vo.AdminLogType;
import cool.houge.mahu.admin.oas.vo.SysAdminUpsertRequest;
import cool.houge.mahu.admin.sys.service.AdminService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// 管理员
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class AdminController implements HAdminService, WebSupport {

    final VoBeanMapper beanMapper;
    final AdminService adminService;

    @Override
    public void createSysAdmin(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(SysAdminUpsertRequest.class);
        validate(vo);

        var entity = beanMapper.toAdmin(vo);
        adminService.save(entity);
        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void deleteSysAdmin(ServerRequest request, ServerResponse response) {
        var id = adminId(request);
        adminService.delete(id);
        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void getSysAdmin(ServerRequest request, ServerResponse response) {
        var id = adminId(request);
        var entity = adminService.obtainById(id);
        var rs = beanMapper.toSysAdminResponse(entity);
        response.send(rs);
    }

    @Override
    public void pageSysAdmin(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);

        var plist = adminService.findPage(dataFilter);
        var rs = dataFilter.toResult(plist, beanMapper::toSysAdminResponse);
        response.send(rs);
    }

    @Override
    public void pageSysAdminLog(ServerRequest request, ServerResponse response) {
        var dataFilter = dataFilter(request);
        var type = pathArg(request, "type").as(AdminLogType::valueOf).get();
        switch (type) {
            case ACCESS -> {
                var plist = adminService.pageAdminAccessLog(dataFilter);
                var rs = dataFilter.toResult(plist, beanMapper::toAdminAccessLogResponse);
                response.send(rs);
            }
            case AUTH -> {
                var plist = adminService.pageAdminAuthLog(dataFilter);
                var rs = dataFilter.toResult(plist, beanMapper::toAdminAuthLogResponse);
                response.send(rs);
            }
            // AUDIT
            default -> {
                var plist = adminService.pageAdminAuditLog(dataFilter);
                var rs = dataFilter.toResult(plist, beanMapper::toAdminAuditLogResponse);
                response.send(rs);
            }
        }
    }

    @Override
    public void updateSysAdmin(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(SysAdminUpsertRequest.class);
        validate(vo);
        var id = adminId(request);

        var entity = beanMapper.toAdmin(vo).setId(id);
        adminService.update(entity);
        response.status(NO_CONTENT_204).send();
    }

    long adminId(ServerRequest request) {
        return pathArg(request, "admin_id").asLong().get();
    }
}

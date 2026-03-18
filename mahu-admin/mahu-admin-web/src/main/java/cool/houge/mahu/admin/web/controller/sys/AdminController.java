package cool.houge.mahu.admin.web.controller.sys;

import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.sys.query.AdminLogQuery;
import cool.houge.mahu.admin.sys.query.AdminQuery;
import cool.houge.mahu.admin.mapping.SysBeanMapper;
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

    private final SysBeanMapper beanMapper;
    private final AdminService adminService;

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
        var id = pathAdminId(request);
        adminService.delete(id);
        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void getSysAdmin(ServerRequest request, ServerResponse response) {
        var id = pathAdminId(request);
        var entity = adminService.obtainById(id);
        var rs = beanMapper.toSysAdminResponse(entity);
        response.send(rs);
    }

    @Override
    public void pageSysAdmin(ServerRequest request, ServerResponse response) {
        var query = AdminQuery.builder();
        queryIntList(request, "status").ifPresent(query::statusList);
        queryArg(request, "username").ifPresent(query::username);

        var plist = adminService.findPage(query.build(), page(request));
        var rs = beanMapper.toPageResponse(plist, beanMapper::toSysAdminResponse);
        response.send(rs);
    }

    @Override
    public void pageSysAdminLog(ServerRequest request, ServerResponse response) {
        var type = pathArg(request, "type").as(AdminLogType::valueOf).get();
        var adminId = queryInt(request, "admin_id").orElse(null);
        var createdAtRange = queryDateRange(request);
        var page = page(request);
        var logQuery = AdminLogQuery.builder().adminId(adminId).createdAtRange(createdAtRange).build();
        switch (type) {
            case ACCESS -> {
                var plist = adminService.pageAdminAccessLog(logQuery, page);
                var rs = beanMapper.toPageResponse(plist, beanMapper::toAdminAccessLogResponse);
                response.send(rs);
            }
            case AUTH -> {
                var plist = adminService.pageAdminAuthLog(logQuery, page);
                var rs = beanMapper.toPageResponse(plist, beanMapper::toAdminAuthLogResponse);
                response.send(rs);
            }
            // CHANGE
            default -> {
                var plist = adminService.pageAdminChangeLog(logQuery, page);
                var rs = beanMapper.toPageResponse(plist, beanMapper::toAdminChangeLogResponse);
                response.send(rs);
            }
        }
    }

    @Override
    public void updateSysAdmin(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(SysAdminUpsertRequest.class);
        validate(vo);
        var id = pathAdminId(request);

        var entity = beanMapper.toAdmin(vo).setId(id);
        adminService.update(entity);
        response.status(NO_CONTENT_204).send();
    }

    int pathAdminId(ServerRequest request) {
        return pathInt(request, "admin_id");
    }
}

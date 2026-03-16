package cool.houge.mahu.admin.controller;

import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.mapping.VoBeanMapper;
import cool.houge.mahu.admin.oas.controller.HMeService;
import cool.houge.mahu.admin.oas.vo.MePasswordUpdateRequest;
import cool.houge.mahu.admin.oas.vo.MeProfileUpdateRequest;
import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.admin.sys.service.AdminService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// 个人信息
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class MeController implements HMeService, WebSupport {

    private final VoBeanMapper beanMapper;
    private final AdminService adminService;

    @Override
    public void getMeProfile(ServerRequest request, ServerResponse response) {
        var ac = AuthContext.current();
        var dto = adminService.getProfile(ac.adminId());
        dto.setPermissionCodes(ac.permissions());

        var rs = beanMapper.toMeProfileResponse(dto);
        response.send(rs);
    }

    @Override
    public void updateMeProfile(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(MeProfileUpdateRequest.class);
        validate(vo);

        var ac = AuthContext.current();
        var user = beanMapper.toAdmin(vo).setId(ac.adminId());
        adminService.update(user);

        response.status(NO_CONTENT_204).send();
    }

    @Override
    public void updateMePassword(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(MePasswordUpdateRequest.class);
        validate(vo);

        var ac = AuthContext.current();
        var entity = beanMapper.toAdmin(vo).setId(ac.adminId());
        adminService.updatePassword(entity);

        response.status(NO_CONTENT_204).send();
    }
}

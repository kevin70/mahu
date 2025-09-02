package cool.houge.mahu.admin.controller;

import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.UpdateMePasswordRequest;
import cool.houge.mahu.admin.oas.model.UpdateMeProfileRequest;
import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.admin.system.service.AdminService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// 个人信息
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class MeController implements WebSupport {

    final VoBeanMapper beanMapper;
    final AdminService adminService;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/me/profile", s(this::getMeProfile));
        rules.put("/me/profile", s(this::updateMeProfile));
        rules.put("/me/password", s(this::updateMePassword));
    }

    void getMeProfile(ServerRequest request, ServerResponse response) {
        var ac = AuthContext.current();
        var dto = adminService.getProfile(ac.uid());
        dto.setPermits(ac.permissions());

        var rs = beanMapper.toGetMeProfileResponse(dto);
        response.send(rs);
    }

    void updateMeProfile(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpdateMeProfileRequest.class);
        validate(vo);

        var ac = AuthContext.current();
        var user = beanMapper.toAdmin(vo).setId(ac.uid());
        adminService.update(user);

        response.status(NO_CONTENT_204).send();
    }

    void updateMePassword(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpdateMePasswordRequest.class);
        validate(vo);

        var ac = AuthContext.current();
        var entity = beanMapper.toAdmin(vo).setId(ac.uid());
        adminService.updatePassword(entity);

        response.status(NO_CONTENT_204).send();
    }
}

package cool.houge.mahu.admin.controller;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.UpdateMePasswordRequest;
import cool.houge.mahu.admin.oas.model.UpdateMeProfileRequest;
import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.admin.system.service.EmployeeService;
import cool.houge.mahu.common.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static io.helidon.http.Status.NO_CONTENT_204;
import static io.helidon.webserver.http.SecureHandler.authenticate;

/// 个人信息
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class MeController implements HttpService, WebSupport {

    @Inject
    VoBeanMapper beanMapper;

    @Inject
    EmployeeService employeeService;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/me/profile", authenticate().wrap(this::getMeProfile));
        rules.put("/me/profile", authenticate().wrap(this::updateMeProfile));
        rules.put("/me/password", authenticate().wrap(this::updateMePassword));
    }

    void getMeProfile(ServerRequest request, ServerResponse response) {
        var ac = AuthContext.get();
        var dto = employeeService.getProfile(ac.uid());
        dto.setPermits(ac.permits());

        var rs = beanMapper.toGetMeProfileResponse(dto);
        response.send(rs);
    }

    void updateMeProfile(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpdateMeProfileRequest.class);
        validate(vo);

        var ac = AuthContext.get();
        var user = beanMapper.toEmployee(vo).setId(ac.uid());
        employeeService.update(user);

        response.status(NO_CONTENT_204).send();
    }

    void updateMePassword(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpdateMePasswordRequest.class);
        validate(vo);

        var ac = AuthContext.get();
        var entity = beanMapper.toEmployee(vo).setId(ac.uid());
        employeeService.updatePassword(entity);

        response.status(NO_CONTENT_204).send();
    }
}

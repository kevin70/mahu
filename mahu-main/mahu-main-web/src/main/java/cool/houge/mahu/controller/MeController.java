package cool.houge.mahu.controller;

import cool.houge.mahu.common.web.WebSupport;
import cool.houge.mahu.internal.VoBeanMapper;
import cool.houge.mahu.oas.model.UpdateMeProfileRequest;
import cool.houge.mahu.security.AuthContext;
import cool.houge.mahu.service.UserService;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static io.helidon.http.Status.NO_CONTENT_204;

/// 个人信息接口
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class MeController implements HttpService, WebSupport {

    @Inject
    UserService userService;

    @Inject
    VoBeanMapper beanMapper;

    @Override
    public void routing(HttpRules rules) {
        rules.get("/me/profile", authz().wrap(this::getMeProfile));
        rules.put("/me/profile", authz().wrap(this::updateMeProfile));
    }

    private void getMeProfile(ServerRequest request, ServerResponse response) {
        var ac = AuthContext.get();
        var user = userService.getProfile(ac.uid());
        var rs = beanMapper.toGetMeProfileResponse(user);
        response.send(rs);
    }

    private void updateMeProfile(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpdateMeProfileRequest.class);
        validate(vo);

        var ac = AuthContext.get();
        var entity = beanMapper.toUser(vo).setId(ac.uid());
        userService.update(entity);

        response.status(NO_CONTENT_204).send();
    }
}

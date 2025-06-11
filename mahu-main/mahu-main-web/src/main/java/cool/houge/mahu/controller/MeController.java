package cool.houge.mahu.controller;

import static io.helidon.http.Status.NO_CONTENT_204;

import cool.houge.mahu.internal.VoBeanMapper;
import cool.houge.mahu.oas.model.UpdateMeProfileRequest;
import cool.houge.mahu.security.AuthContext;
import cool.houge.mahu.service.UserService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

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
        rules.get("/me/profile", s(this::getMeProfile));
        rules.put("/me/profile", s(this::updateMeProfile));
    }

    private void getMeProfile(ServerRequest request, ServerResponse response) {
        var ac = AuthContext.current();
        var user = userService.getProfile(ac.uid());
        var rs = beanMapper.toGetMeProfileResponse(user);
        response.send(rs);
    }

    private void updateMeProfile(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(UpdateMeProfileRequest.class);
        validate(vo);

        var ac = AuthContext.current();
        var entity = beanMapper.toUser(vo).setId(ac.uid());
        userService.update(entity);

        response.status(NO_CONTENT_204).send();
    }
}

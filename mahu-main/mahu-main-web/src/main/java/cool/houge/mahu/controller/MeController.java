package cool.houge.mahu.controller;

import cool.houge.mahu.common.web.WebSupport;
import cool.houge.mahu.internal.VoBeanMapper;
import cool.houge.mahu.service.UserService;
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
        rules.get("/me/profile", authz().wrap(this::getMeProfile));
    }

    private void getMeProfile(ServerRequest request, ServerResponse response) {
        var user = userService.getProfile(uid());
        var rs = beanMapper.toGetMeProfileResponse(user);
        response.send(rs);
    }
}

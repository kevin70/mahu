package cool.houge.mahu.controller;

import com.google.common.base.Strings;
import cool.houge.lang.BizCodeException;
import cool.houge.lang.BizCodes;
import cool.houge.mahu.common.GrantType;
import cool.houge.mahu.common.web.WebSupport;
import cool.houge.mahu.internal.VoBeanMapper;
import cool.houge.mahu.oas.model.GetTokenRequest;
import cool.houge.mahu.service.TokenPayload;
import cool.houge.mahu.service.TokenService;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 登录
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class LoginController implements HttpService, WebSupport {

    @Inject
    VoBeanMapper beanMapper;

    @Inject
    TokenService tokenService;

    @Override
    public void routing(HttpRules rules) {
        rules.post("/login/token", this::getToken);
    }

    private void getToken(ServerRequest request, ServerResponse response) {
        var vo = request.content().as(GetTokenRequest.class);
        var grantType = vo.getGrantType();
        TokenPayload payload;
        if (GrantType.REFRESH_TOKEN.code.equals(grantType)) {
            var form = beanMapper.toTokenRefreshTokenForm(vo);
            validate(form);
            payload = beanMapper.toTokenPayload(form);
        } else if (GrantType.WECHAT_XCX.code.equals(grantType)) {
            var form = beanMapper.toTokenWechatXcxForm(vo);
            validate(form);
            payload = beanMapper.toTokenPayload(form);
        } else {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, Strings.lenientFormat("非法的授权类型[%s]", grantType));
        }

        var tokenResult = tokenService.login(payload);
        var rs = beanMapper.toGetTokenResponse(tokenResult);
        response.send(rs);
    }
}

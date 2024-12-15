package cool.houge.mahu.admin.controller;

import cool.houge.lang.BizCodeException;
import cool.houge.lang.BizCodes;
import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.LoginRequest;
import cool.houge.mahu.admin.system.dto.TokenPayload;
import cool.houge.mahu.admin.system.service.TokenService;
import cool.houge.mahu.common.GrantType;
import cool.houge.mahu.common.web.WebSupport;
import io.helidon.common.parameters.Parameters;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static com.google.common.base.Strings.lenientFormat;

/// [获取访问令牌](https://oauth.net/2/)
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
        rules.post("/oauth2/token", this::getOAuth2Token);
    }

    void getToken(ServerRequest request, ServerResponse response) {
        var body = request.content().as(LoginRequest.class);

        TokenPayload payload;
        if (GrantType.PASSWORD.code.equals(body.getGrantType())) {
            var bean = beanMapper.toTokenPasswordForm(body);
            validate(bean);
            payload = beanMapper.toTokenPayload(bean, GrantType.PASSWORD);
        } else if (GrantType.REFRESH_TOKEN.code.equals(body.getGrantType())) {
            var bean = beanMapper.toTokenRefreshTokenForm(body);
            validate(bean);
            payload = beanMapper.toTokenPayload(bean, GrantType.REFRESH_TOKEN);
        } else {
            // 未实现
            throw new BizCodeException(BizCodes.UNIMPLEMENTED, lenientFormat("不支持授权类型[%s]", body.getGrantType()));
        }

        payload.setClientIp(clientAddr(request));
        var rs = beanMapper.toGetTokenResponse(tokenService.token(payload));
        response.send(rs);
    }

    void getOAuth2Token(ServerRequest request, ServerResponse response) {
        var form = request.content().as(Parameters.class);
        var body = new LoginRequest();
        body.setGrantType(form.get("grant_type"));
        body.setUsername(form.get("username"));
        body.setPassword(form.get("password"));

        TokenPayload payload;
        if (GrantType.PASSWORD.code.equals(body.getGrantType())) {
            var bean = beanMapper.toTokenPasswordForm(body);
            validate(bean);
            payload = beanMapper.toTokenPayload(bean, GrantType.PASSWORD);
        } else {
            // 未实现
            throw new BizCodeException(BizCodes.UNIMPLEMENTED, lenientFormat("不支持授权类型[%s]", body.getGrantType()));
        }

        var rs = beanMapper.toGetTokenResponse(tokenService.token(payload));
        rs.setTokenType("Bearer");
        response.send(rs);
    }
}

package cool.houge.mahu.admin.controller;

import static com.google.common.base.Strings.lenientFormat;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.oas.model.LoginRequest;
import cool.houge.mahu.admin.system.dto.TokenPayload;
import cool.houge.mahu.admin.system.service.TokenService;
import cool.houge.mahu.util.GrantType;
import cool.houge.mahu.util.Metadata;
import cool.houge.mahu.web.WebSupport;
import io.helidon.common.parameters.Parameters;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// [获取访问令牌](https://oauth.net/2/)
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class LoginController implements HttpService, WebSupport {

    private final VoBeanMapper beanMapper;
    private final TokenService tokenService;

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

        payload.setClientIp(Metadata.current().clientAddr());
        var rs = beanMapper.toTokenResponse(tokenService.token(payload));
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

        var rs = beanMapper.toTokenResponse(tokenService.token(payload));
        rs.setTokenType("Bearer");
        response.send(rs);
    }
}

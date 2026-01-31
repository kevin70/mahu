package cool.houge.mahu.admin.controller;

import static com.google.common.base.Strings.lenientFormat;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.oas.controller.HLoginService;
import cool.houge.mahu.admin.oas.vo.LoginTokenRequest;
import cool.houge.mahu.admin.sys.dto.TokenPayload;
import cool.houge.mahu.admin.sys.service.TokenService;
import cool.houge.mahu.util.GrantType;
import cool.houge.mahu.util.Metadata;
import cool.houge.mahu.web.WebSupport;
import io.helidon.service.registry.Service;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;

/// [获取访问令牌](https://oauth.net/2/)
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@AllArgsConstructor
public class LoginController implements HLoginService, WebSupport {

    private final VoBeanMapper beanMapper;
    private final TokenService tokenService;

    @Override
    public void login(ServerRequest request, ServerResponse response) {
        var body = request.content().as(LoginTokenRequest.class);

        TokenPayload payload;
        if (GrantType.PASSWORD.matches(body.getGrantType())) {
            var bean = beanMapper.toTokenPasswordForm(body);
            validate(bean);
            payload = beanMapper.toTokenPayload(bean, GrantType.PASSWORD);
        } else if (GrantType.REFRESH_TOKEN.matches(body.getGrantType())) {
            var bean = beanMapper.toTokenRefreshTokenForm(body);
            validate(bean);
            payload = beanMapper.toTokenPayload(bean, GrantType.REFRESH_TOKEN);
        } else {
            // 未实现
            throw new BizCodeException(BizCodes.UNIMPLEMENTED, lenientFormat("不支持授权类型[%s]", body.getGrantType()));
        }

        payload.setClientIp(Metadata.current().clientAddr());
        var rs = beanMapper.toLoginTokenResponse(tokenService.token(payload));
        response.send(rs);
    }
}

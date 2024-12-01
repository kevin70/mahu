package cool.houge.mahu.service;

import cool.houge.mahu.common.GrantType;
import cool.houge.mahu.common.security.AuthContext;
import cool.houge.mahu.common.security.TokenVerifier;
import io.ebean.annotation.Transactional;
import jakarta.inject.Singleton;

/// 令牌
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class TokenService implements TokenVerifier {

    @Override
    public AuthContext verify(String token) {
        return null;
    }

    @Transactional
    public void login(TokenPayload payload) {
        if (payload.getGrantType() == GrantType.REFRESH_TOKEN) {
            //
        }
    }

    void loginByRefreshToken(TokenPayload payload) {
        //
    }

    void loginByWechatXcx(TokenPayload payload) {
        //
    }
}

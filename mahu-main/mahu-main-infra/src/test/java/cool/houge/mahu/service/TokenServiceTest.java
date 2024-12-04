package cool.houge.mahu.service;

import cool.houge.mahu.TestMetadataBean;
import cool.houge.mahu.TestTransactionBase;
import cool.houge.mahu.entity.User;
import io.helidon.security.jwt.EncryptedJwt;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.gen;
import static org.instancio.Instancio.of;

/// @author ZY (kzou227@qq.com)
class TokenServiceTest extends TestTransactionBase {

    @Spy
    TokenService tokenService;

    TokenResult getTokenResult() {
        var payload = Instancio.create(TokenPayload.class);
        var user = Instancio.create(User.class);
        return tokenService.makeToken(new TestMetadataBean(), payload, user);
    }

    @Test
    void verify() {
        var payload = Instancio.create(TokenPayload.class);
        var user = Instancio.create(User.class);
        var token = tokenService.makeToken(new TestMetadataBean(), payload, user);

        var ac = tokenService.verify(token.getAccessToken());
        assertThat(ac).isNotNull();
        assertThat(ac.uid()).isEqualTo(user.getId());
    }

    @Test
    void makeToken() {
        var payload = Instancio.create(TokenPayload.class);
        var user = Instancio.create(User.class);

        var token = tokenService.makeToken(new TestMetadataBean(), payload, user);
        assertThat(token).isNotNull();
        assertThat(token.getExpiresIn())
                .isEqualTo(tokenService.tokenConfig.accessExpires().toSeconds());

        var jwt = EncryptedJwt.parseToken(token.getAccessToken())
                .decrypt(tokenService.jwkKeys)
                .getJwt();
        assertThat(jwt.userPrincipal()).hasValue(user.getId().toString());
    }

    @Test
    void loginByWechatXcx() {
        var appid = gen().uuid().get().toString();
        var openid = gen().uuid().get().toString();
        var nicknameAvatar = of(TokenService.NicknameAvatar.class).create();

        var user = tokenService.upsertWechatUser(appid, null, openid, () -> nicknameAvatar);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isPositive();
        assertThat(user.getNickname()).isNotNull().isEqualTo(nicknameAvatar.nickname());
        assertThat(user.getAvatar()).isNotNull().isEqualTo(nicknameAvatar.avatar());

        assertThat(user.getWechatProfile().getAppid()).isEqualTo(appid);
        assertThat(user.getWechatProfile().getOpenid()).isEqualTo(openid);
    }

    @Test
    void loginByWechatXcx_unionid() {
        var appid = gen().uuid().get().toString();
        var unionid = gen().uuid().get().toString();
        var openid = gen().uuid().get().toString();
        var nicknameAvatar = of(TokenService.NicknameAvatar.class).create();

        var user = tokenService.upsertWechatUser(appid, unionid, openid, () -> nicknameAvatar);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isPositive();
        assertThat(user.getNickname()).isNotNull().isEqualTo(nicknameAvatar.nickname());
        assertThat(user.getAvatar()).isNotNull().isEqualTo(nicknameAvatar.avatar());

        assertThat(user.getWechatProfile().getAppid()).isEqualTo(appid);
        assertThat(user.getWechatProfile().getUnionid()).isEqualTo(unionid);
        assertThat(user.getWechatProfile().getOpenid()).isEqualTo(openid);
    }
}

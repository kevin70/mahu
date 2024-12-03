package cool.houge.mahu.service;

import cool.houge.mahu.TestTransactionBase;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.gen;
import static org.instancio.Instancio.of;

/// @author ZY (kzou227@qq.com)
class TokenServiceTest extends TestTransactionBase {

    @Spy
    TokenService tokenService;

    @Test
    void loginByWechatXcx() {
        var appid = gen().uuid().get().toString();
        String unionid = null;
        var openid = gen().uuid().get().toString();
        var nicknameAvatar = of(TokenService.NicknameAvatar.class).create();

        var user = tokenService.upsertWechatUser(appid, null, openid, () -> nicknameAvatar);

        assertThat(user).isNotNull()
            .hasFieldOrPropertyWithValue("nickname", nicknameAvatar.nickname())
            .hasFieldOrPropertyWithValue("avatar", nicknameAvatar.avatar());
        assertThat(user.getWechatProfile())
            .hasFieldOrPropertyWithValue("appid", appid)
            .hasFieldOrPropertyWithValue("unionid", unionid)
            .hasFieldOrPropertyWithValue("openid", openid);
    }
}

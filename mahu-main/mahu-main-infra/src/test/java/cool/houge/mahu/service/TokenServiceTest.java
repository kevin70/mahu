package cool.houge.mahu.service;

import com.github.f4b6a3.ulid.Ulid;
import cool.houge.mahu.TestMetadataBean;
import cool.houge.mahu.TestTransactionBase;
import cool.houge.mahu.common.BizCodeException;
import cool.houge.mahu.common.BizCodes;
import cool.houge.mahu.common.GrantType;
import cool.houge.mahu.entity.User;
import cool.houge.mahu.repository.UserRepository;
import io.helidon.security.jwt.EncryptedJwt;
import io.helidon.security.jwt.Jwt;
import io.helidon.security.jwt.SignedJwt;
import io.helidon.security.jwt.jwk.Jwk;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.instancio.Instancio.gen;
import static org.instancio.Instancio.of;
import static org.mockito.Mockito.doReturn;

/// @author ZY (kzou227@qq.com)
class TokenServiceTest extends TestTransactionBase {

    @Spy
    TokenService tokenService;

    @Spy
    UserRepository userRepository;

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
    void verify_illegal_token() {
        var token = "abcefsdfsafkljsakljfjk;lj;";
        assertThatExceptionOfType(BizCodeException.class)
                .isThrownBy(() -> tokenService.verify(token))
                .extracting(BizCodeException::getCode)
                .isEqualTo(BizCodes.UNAUTHENTICATED);
    }

    @Test
    void verify_expired_token() {
        var atJwt = Jwt.builder()
                .jwtId(Ulid.fast().toLowerCase())
                .userPrincipal("0")
                .issueTime(Instant.now())
                .expirationTime(Instant.now().minusSeconds(6000))
                .nonce(Ulid.fast().toLowerCase())
                .build();
        var jwk = tokenService.obtainJwk();
        var accessToken = EncryptedJwt.builder(SignedJwt.sign(atJwt, Jwk.NONE_JWK))
                .jwks(tokenService.jwkKeys, jwk.keyId())
                .build();
        var token = accessToken.token();

        assertThatExceptionOfType(BizCodeException.class)
                .isThrownBy(() -> tokenService.verify(token))
                .extracting(BizCodeException::getCode)
                .isEqualTo(BizCodes.UNAUTHENTICATED);
    }

    @Test
    @DisplayName("登录已禁止的帐户")
    void login_user_status_blocked() {
        var payload = Instancio.create(TokenPayload.class).setGrantType(GrantType.PASSWORD);
        var user = Instancio.create(User.class).setStatus(User.Status.BLOCKED);

        doReturn(user).when(tokenService).loginByUsername(payload);

        assertThatExceptionOfType(BizCodeException.class)
                .isThrownBy(() -> tokenService.login(payload))
                .extracting(BizCodeException::getCode)
                .isEqualTo(BizCodes.FAILED_PRECONDITION);

        Mockito.verify(tokenService).loginByUsername(payload);
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
        assertThat(user.getStatus()).isNotNull().isEqualTo(User.Status.NORMAL);

        assertThat(user.getWechatAppid()).isEqualTo(appid);
        assertThat(user.getWechatOpenid()).isEqualTo(openid);
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
        assertThat(user.getStatus()).isNotNull().isEqualTo(User.Status.NORMAL);

        assertThat(user.getWechatAppid()).isEqualTo(appid);
        assertThat(user.getWechatOpenid()).isEqualTo(openid);
        assertThat(user.getWechatUnionid()).isEqualTo(unionid);
    }

    @Test
    void loginByRefreshToken() {
        var payload = Instancio.create(TokenPayload.class);
        var user = Instancio.create(User.class);
        var token = tokenService.makeToken(new TestMetadataBean(), payload, user);

        doReturn(user).when(userRepository).findById(user.getId());

        var dbEntity = tokenService.loginByRefreshToken(
                new TokenPayload().setGrantType(GrantType.REFRESH_TOKEN).setRefreshToken(token.getRefreshToken()));
        assertThat(dbEntity).isEqualTo(user);

        Mockito.verify(userRepository).findById(user.getId());
    }

    @Test
    void loginByRefreshToken_expired_token() {
        var refreshToken = "eyJhbGciOiJSU0EtT0FFUCIsImVuYyI6IkEyNTZHQ00iLCJjdHkiOiJKV1QiLCJraWQiOiJx"
                + "dWdWSFBfa0x6dE9MU1NsRmlaQVoifQ.Xp4S9aqbOu4-uDl5h_bTs8qN14dFEWluBmCLipMkesd7res2dxApv3L3"
                + "8bsQnnFBRdskCbg4hOa7RO2yTR4ZK9f_hSH0PM5PaGuOzlvjMfm7kwjUkvZpAK8-rxFvRgHf_ZUslF7YKWJkOhP"
                + "s4M2XR2yX7sizJS7xLBLYMKjoi_w_eD0jpNB4iiDE7NwYk2d_2CKO6IpYlyY3wP9D4OcfiZrOPOQZIMHeaZ37nT"
                + "NSI1dumMn7BS9lSPcJCmNnvN6V6KQqjSvhGd6ssLqKtx1dwYCdp3aqcxRUNuASEUurGmLBo584rNQdZVStC6GXP"
                + "aZ5w8EQDWF267rYGLJP-5w8jQ.odCD_1k3BYSZdtHY.k6lPCVJ6JYb1HS9xTVbOq4RQuqt4k8KcMxFdywBNOchf"
                + "E8C0c9744wZAnOtPxZUQ0Gm4ScwVIjm4VtnYVzKnT4LB6YchfzDbcC1lqwAaStIQ6uSaTT69MIHBKjsIE4soKKQ"
                + "ZT3kQl9vRuvWwtxH3uUhYlVXNY2XEfZ2rsQMAf1T3eMFGcnNujStPqANhUk0f-wazdxR5D4EyAxF0OlMcTK8.__"
                + "601eZB300OSdU_oCZmOA";

        assertThatExceptionOfType(BizCodeException.class)
                .isThrownBy(() -> tokenService.loginByRefreshToken(
                        new TokenPayload().setGrantType(GrantType.REFRESH_TOKEN).setRefreshToken(refreshToken)))
                .extracting(BizCodeException::getCode)
                .isEqualTo(BizCodes.INVALID_ARGUMENT);
    }

    @Test
    void loginByRefreshToken_illegal_token() {
        var refreshToken = "eyJhbGciOiJSU0EtT0FFUCIsImVuYyI6IkEyNTZHQ00iLCJjdHkiOiJKV1QiLCJraWQiOiJx";

        assertThatExceptionOfType(BizCodeException.class)
                .isThrownBy(() -> tokenService.loginByRefreshToken(
                        new TokenPayload().setGrantType(GrantType.REFRESH_TOKEN).setRefreshToken(refreshToken)))
                .extracting(BizCodeException::getCode)
                .isEqualTo(BizCodes.INVALID_ARGUMENT);
    }
}

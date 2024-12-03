package cool.houge.mahu.service;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.google.common.base.Strings;
import cool.houge.mahu.common.GrantType;
import cool.houge.mahu.common.security.AuthContext;
import cool.houge.mahu.common.security.TokenVerifier;
import cool.houge.mahu.config.TokenConfig;
import cool.houge.mahu.entity.User;
import cool.houge.mahu.entity.WechatProfile;
import cool.houge.mahu.entity.system.Client;
import cool.houge.mahu.remote.wechat.Jscode2SessionPayload;
import cool.houge.mahu.remote.wechat.WechatClient;
import cool.houge.mahu.remote.wechat.WechatEncryptPayload;
import cool.houge.mahu.repository.UserRepository;
import cool.houge.mahu.system.repository.ClientRepository;
import io.ebean.annotation.Transactional;
import io.helidon.security.jwt.EncryptedJwt;
import io.helidon.security.jwt.Jwt;
import io.helidon.security.jwt.SignedJwt;
import io.helidon.security.jwt.jwk.Jwk;
import io.helidon.security.jwt.jwk.JwkKeys;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/// 令牌
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class TokenService implements TokenVerifier {

    private static final Logger log = LogManager.getLogger(TokenService.class);

    @Inject
    JwkKeys jwkKeys;

    @Inject
    TokenConfig tokenConfig;

    @Inject
    ClientRepository clientRepository;

    @Inject
    WechatClient wechatClient;

    @Inject
    UserRepository userRepository;

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

    void makeToken(Client client, User user) {
        var jwk = obtainJwk();
        var jwtId = UlidCreator.getUlid().toLowerCase();
        var sub = String.valueOf(user.getId());
        var iat = Instant.now();
        var atJwt = Jwt.builder()
                .jwtId(jwtId)
                .subject(sub)
                .issueTime(iat)
                .expirationTime(iat.plus(tokenConfig.accessExpires()))
                .addAudience(client.getClientId())
                .nonce(Ulid.fast().toLowerCase())
                .build();
        var accessToken = EncryptedJwt.builder(SignedJwt.sign(atJwt, Jwk.NONE_JWK))
                .jwks(jwkKeys, jwk.keyId())
                .build();

        var rtJwt = Jwt.builder()
                .jwtId(jwtId)
                .subject(sub)
                .issueTime(iat)
                .expirationTime(iat.plus(tokenConfig.refreshExpires()))
                .nonce(Ulid.fast().toLowerCase())
                .build();
        var refreshToken = EncryptedJwt.builder(SignedJwt.sign(rtJwt, Jwk.NONE_JWK))
                .jwks(jwkKeys, jwk.keyId())
                .build();
    }

    Jwk obtainJwk() {
        var keys = jwkKeys.keys();
        var ran = ThreadLocalRandom.current();
        var i = ran.nextInt(keys.size());
        return keys.get(i);
    }

    User loginByWechatXcx(TokenPayload payload) {
        var client = clientRepository.obtainClient(payload.getClientId());
        var sessionPayload = new Jscode2SessionPayload()
                .setAppid(client.getWechatAppid())
                .setSecret(client.getWechatAppsecret())
                .setJsCode(payload.getWechatJsCode());
        var sessionResult = wechatClient.jscode2Session(sessionPayload);

        var user =
                upsertWechatUser(client.getWechatAppid(), sessionResult.getUnionid(), sessionResult.getOpenid(), () -> {
                    var decryptResult = wechatClient.decrypt(new WechatEncryptPayload()
                            .setAppid(client.getWechatAppid())
                            .setEncryptedData(payload.getWechatEncryptData())
                            .setIv(payload.getWechatIv())
                            .setSessionKey(sessionResult.getSessionKey()));
                    return new NicknameAvatar(decryptResult.getNickName(), decryptResult.getAvatarUrl());
                });

        log.debug("微信小程序用户认证成功 userId={}", user.getId());
        return user;
    }

    User upsertWechatUser(String appid, String unionid, String openid, Supplier<NicknameAvatar> nicknameAvatar) {
        User user;
        if (Strings.isNullOrEmpty(unionid)) {
            user = userRepository.findByWechatAppid$Openid(appid, openid);
        } else {
            user = userRepository.findByWechatUnionid(unionid);
        }

        // 数据库中没有则保存数据
        if (user == null) {
            // 保存用户
            var wechatProfile =
                    new WechatProfile().setAppid(appid).setOpenid(openid).setUnionid(unionid);

            var info = nicknameAvatar.get();
            user = new User().setNickname(info.nickname).setAvatar(info.avatar).setWechatProfile(wechatProfile);
            userRepository.save(user);
        }
        return user;
    }

    record NicknameAvatar(String nickname, String avatar) {}
}

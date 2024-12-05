package cool.houge.mahu.service;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.google.common.base.Strings;
import cool.houge.lang.BizCodeException;
import cool.houge.lang.BizCodes;
import cool.houge.mahu.common.GrantType;
import cool.houge.mahu.common.Metadata;
import cool.houge.mahu.common.security.AuthContext;
import cool.houge.mahu.common.security.TokenVerifier;
import cool.houge.mahu.config.TokenConfig;
import cool.houge.mahu.entity.User;
import cool.houge.mahu.entity.WechatProfile;
import cool.houge.mahu.entity.system.TokenJour;
import cool.houge.mahu.remote.wechat.Jscode2SessionPayload;
import cool.houge.mahu.remote.wechat.WechatClient;
import cool.houge.mahu.remote.wechat.WechatEncryptPayload;
import cool.houge.mahu.repository.UserRepository;
import cool.houge.mahu.system.repository.ClientRepository;
import cool.houge.mahu.system.repository.TokenJourRepository;
import io.ebean.annotation.Transactional;
import io.helidon.common.Errors;
import io.helidon.common.LazyValue;
import io.helidon.security.jwt.EncryptedJwt;
import io.helidon.security.jwt.Jwt;
import io.helidon.security.jwt.JwtValidator;
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
import java.util.stream.Collectors;

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

    @Inject
    TokenJourRepository tokenJourRepository;

    @Override
    public AuthContext verify(String token) {
        Jwt jwt;
        try {
            jwt = EncryptedJwt.parseToken(token).decrypt(jwkKeys).getJwt();
            var validator = JwtValidator.builder().addExpirationValidator().build();
            var errors = validator.validate(jwt);
            if (errors.hasFatal()) {
                var msg = errors.stream().map(Errors.ErrorMessage::getMessage).collect(Collectors.joining("|"));
                throw new BizCodeException(BizCodes.UNAUTHENTICATED, msg);
            }
        } catch (Exception e) {
            throw new BizCodeException(BizCodes.UNAUTHENTICATED, "非法的访问令牌");
        }

        var userId = jwt.userPrincipal()
                .map(Long::valueOf)
                .orElseThrow(() -> new BizCodeException(BizCodes.UNAUTHENTICATED, "非法的访问令牌"));
        var userLv = LazyValue.create(() -> userRepository.findById(userId));
        return new AuthContext() {

            @Override
            public long uid() {
                return userId;
            }

            @Override
            public String name() {
                return userLv.get().getNickname();
            }

            @Override
            public boolean containsPermits(String... permits) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Transactional
    public TokenResult login(TokenPayload payload) {
        User user;
        if (payload.getGrantType() == GrantType.REFRESH_TOKEN) {
            user = loginByRefreshToken(payload);
        } else if (payload.getGrantType() == GrantType.WECHAT_XCX) {
            user = loginByWechatXcx(payload);
        } else {
            throw new IllegalArgumentException("Unsupported grant type: " + payload.getGrantType());
        }
        return makeToken(Metadata.get(), payload, user);
    }

    @Transactional
    TokenResult makeToken(Metadata metadata, TokenPayload payload, User user) {
        var jwk = obtainJwk();
        var jwtId = UlidCreator.getUlid().toLowerCase();
        var sub = String.valueOf(user.getId());
        var iat = Instant.now();
        var atJwt = Jwt.builder()
                .jwtId(jwtId)
                .userPrincipal(sub)
                .issueTime(iat)
                .expirationTime(iat.plus(tokenConfig.accessExpires()))
                .addAudience(payload.getClientId())
                .nonce(Ulid.fast().toLowerCase())
                .build();
        var accessToken = EncryptedJwt.builder(SignedJwt.sign(atJwt, Jwk.NONE_JWK))
                .jwks(jwkKeys, jwk.keyId())
                .build();

        var rtJwt = Jwt.builder()
                .jwtId(jwtId)
                .userPrincipal(sub)
                .issueTime(iat)
                .expirationTime(iat.plus(tokenConfig.refreshExpires()))
                .nonce(Ulid.fast().toLowerCase())
                .build();
        var refreshToken = EncryptedJwt.builder(SignedJwt.sign(rtJwt, Jwk.NONE_JWK))
                .jwks(jwkKeys, jwk.keyId())
                .build();

        // 保存登录记录
        var tokenJour = new TokenJour()
                .setId(jwtId)
                .setSub(sub)
                .setClientId(payload.getClientId())
                .setClientAddr(metadata.clientAddr())
                .setGrantType(payload.getGrantType().getCode());
        tokenJourRepository.save(tokenJour);

        return new TokenResult()
                .setExpiresIn(tokenConfig.accessExpires().toSeconds())
                .setAccessToken(accessToken.token())
                .setRefreshToken(refreshToken.token());
    }

    Jwk obtainJwk() {
        var keys = jwkKeys.keys();
        var ran = ThreadLocalRandom.current();
        var i = ran.nextInt(keys.size());
        return keys.get(i);
    }

    @Transactional
    User loginByRefreshToken(TokenPayload payload) {
        Jwt jwt;
        try {
            jwt = EncryptedJwt.parseToken(payload.getRefreshToken())
                    .decrypt(jwkKeys)
                    .getJwt();
            var validator = JwtValidator.builder().addDefaultTimeValidators().build();
            var errors = validator.validate(jwt);
            if (errors.hasFatal()) {
                var msg = errors.stream().map(Errors.ErrorMessage::getMessage).collect(Collectors.joining("|"));
                throw new BizCodeException(BizCodes.INVALID_ARGUMENT, msg);
            }
        } catch (Exception e) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "错误的刷新令牌");
        }

        var userId = jwt.userPrincipal()
                .map(Long::valueOf)
                .orElseThrow(() -> new BizCodeException(BizCodes.UNAUTHENTICATED, "Invalid refresh token"));
        return userRepository.findById(userId);
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

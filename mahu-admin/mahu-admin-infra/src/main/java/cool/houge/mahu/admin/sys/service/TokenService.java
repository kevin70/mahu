package cool.houge.mahu.admin.sys.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.codec.base.Base58BtcCodec;
import com.google.common.base.Strings;
import com.password4j.Password;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.entity.Admin;
import cool.houge.mahu.admin.entity.AdminAuthLog;
import cool.houge.mahu.admin.entity.AdminStatus;
import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.admin.security.TokenVerifier;
import cool.houge.mahu.admin.sys.dto.TokenPayload;
import cool.houge.mahu.admin.sys.dto.TokenResult;
import cool.houge.mahu.admin.sys.repository.AdminAuthLogRepository;
import cool.houge.mahu.admin.sys.repository.AdminRepository;
import cool.houge.mahu.admin.sys.repository.AuthClientRepository;
import cool.houge.mahu.config.ConfigKeys;
import cool.houge.mahu.config.TokenConfig;
import cool.houge.mahu.util.Metadata;
import io.ebean.annotation.Transactional;
import io.helidon.common.LazyValue;
import io.helidon.common.configurable.Resource;
import io.helidon.config.Config;
import io.helidon.security.jwt.EncryptedJwt;
import io.helidon.security.jwt.Jwt;
import io.helidon.security.jwt.JwtValidator;
import io.helidon.security.jwt.SignedJwt;
import io.helidon.security.jwt.jwk.Jwk;
import io.helidon.security.jwt.jwk.JwkKeys;
import io.helidon.service.registry.Service.Singleton;
import java.time.Instant;
import java.util.List;
import java.util.random.RandomGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;

/// 令牌服务
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class TokenService implements TokenVerifier {

    private static final Logger log = LogManager.getLogger();

    private final JwkKeys jwkKeys;
    private final TokenConfig tokenConfig;
    private final AdminRepository adminRepository;
    private final AdminAuthLogRepository adminAuthLogRepository;
    private final AuthClientRepository authClientRepository;

    public TokenService(
            Config root,
            AdminRepository adminRepository,
            AdminAuthLogRepository adminAuthLogRepository,
            AuthClientRepository authClientRepository) {
        this.jwkKeys = JwkKeys.builder()
                .resource(Resource.create(root.get(ConfigKeys.JWT_KEYS)))
                .build();
        this.tokenConfig = TokenConfig.create(root.get(ConfigKeys.TOKEN));
        this.adminRepository = adminRepository;
        this.adminAuthLogRepository = adminAuthLogRepository;
        this.authClientRepository = authClientRepository;
    }

    @Override
    public AuthContext verify(String token) {
        var jwt = parseToken(token);
        var adminId = jwt.userPrincipal()
                .map(Long::valueOf)
                .orElseThrow(() -> new BizCodeException(BizCodes.UNAUTHENTICATED, "非法的访问令牌"));

        LazyValue<@NonNull Admin> adminLv = LazyValue.create(() -> {
            var admin = adminRepository.findById(adminId);
            if (admin == null) {
                throw new BizCodeException(BizCodes.UNAUTHENTICATED, "未找到管理员");
            }
            return admin;
        });

        return new AuthContext() {

            @Override
            public long uid() {
                return adminId;
            }

            @Override
            public String name() {
                return adminLv.get().getNickname();
            }

            @Override
            public boolean hasPermission(String code) {
                var codes = permissions();
                // 拥有超级管理员权限
                if (codes.contains("*")) {
                    return true;
                }
                // 拥有指定的权限代码
                return codes.contains(code);
            }

            @Override
            public List<String> permissions() {
                return List.copyOf(adminLv.get().allPermissions());
            }
        };
    }

    @Transactional
    public TokenResult token(TokenPayload payload) {
        var client = authClientRepository.obtainClient(payload.getClientId());
        var grantType = payload.getGrantType();

        var admin =
                switch (grantType) {
                    case PASSWORD -> loginByUsername(payload);
                    case REFRESH_TOKEN -> loginByRefreshToken(payload);
                    case null, default -> throw new BizCodeException(BizCodes.UNIMPLEMENTED);
                };

        var status = admin.getStatus();
        if (status != AdminStatus.ACTIVE) {
            throw new BizCodeException(BizCodes.PERMISSION_DENIED, "该帐号禁止登录");
        }
        var ret = makeToken(payload, admin);
        log.info("用户成功获取令牌 id={}", admin.getId());

        // 保存登录记录日志
        var metadata = Metadata.current();
        adminAuthLogRepository.save(
                new AdminAuthLog()
                        .setId(UuidCreator.getTimeOrderedEpoch())
                        .setAdminId(admin.getId())
                        .setGrantType(payload.getGrantType().name())
                        .setClientId(client.getClientId())
                        .setIpAddr(metadata.clientAddr())
                        .setUserAgent(metadata.userAgent())
                //
                );
        return ret;
    }

    @Transactional
    TokenResult makeToken(TokenPayload payload, Admin admin) {
        var jwk = obtainJwk();
        var jwtId = Base58BtcCodec.INSTANCE.encode(UuidCreator.getTimeOrderedEpoch());
        var sub = String.valueOf(admin.getId());
        var iat = Instant.now();
        var nonce = String.valueOf(Math.random());
        var atJwt = Jwt.builder()
                .jwtId(jwtId)
                .userPrincipal(sub)
                .issueTime(iat)
                .expirationTime(iat.plus(tokenConfig.accessExpires()))
                .addAudience(payload.getClientId())
                .nonce(nonce)
                .build();
        var accessToken = EncryptedJwt.builder(SignedJwt.sign(atJwt, Jwk.NONE_JWK))
                .jwks(jwkKeys, jwk.keyId())
                .build();

        var rtJwt = Jwt.builder()
                .jwtId(jwtId)
                .userPrincipal(sub)
                .issueTime(iat)
                .expirationTime(iat.plus(tokenConfig.refreshExpires()))
                .nonce(nonce)
                .build();
        var refreshToken = EncryptedJwt.builder(SignedJwt.sign(rtJwt, Jwk.NONE_JWK))
                .jwks(jwkKeys, jwk.keyId())
                .build();

        return new TokenResult()
                .setExpiresIn(tokenConfig.accessExpires().toSeconds())
                .setAccessToken(accessToken.token())
                .setRefreshToken(refreshToken.token());
    }

    @NonNull
    Admin loginByUsername(TokenPayload payload) {
        var user = adminRepository.findByUsername(payload.getUsername());
        if (user == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, Strings.lenientFormat("用户[%s]未找到", payload.getUsername()));
        }

        var checker = Password.check(payload.getPassword(), user.getPassword());
        if (!checker.withArgon2()) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "密码不匹配");
        }
        return user;
    }

    @NonNull
    Admin loginByRefreshToken(TokenPayload payload) {
        var jwt = parseToken(payload.getRefreshToken());
        var sub = jwt.userPrincipal().orElseThrow();
        var user = adminRepository.findById(Long.valueOf(sub));
        if (user == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, Strings.lenientFormat("用户[%s]未找到", sub));
        }
        return user;
    }

    Jwt parseToken(String token) {
        var encryptedJwt = EncryptedJwt.parseToken(token);
        var signedJwt = encryptedJwt.decrypt(jwkKeys);
        var jwt = signedJwt.getJwt();
        var errors = JwtValidator.builder().addDefaultTimeValidators().build().validate(jwt);
        if (errors.hasFatal()) {
            throw new BizCodeException(BizCodes.UNAUTHENTICATED, "访问令牌已经过期");
        }
        return jwt;
    }

    Jwk obtainJwk() {
        var keys = jwkKeys.keys();
        var ran = RandomGenerator.getDefault();
        var i = ran.nextInt(keys.size());
        return keys.get(i);
    }
}

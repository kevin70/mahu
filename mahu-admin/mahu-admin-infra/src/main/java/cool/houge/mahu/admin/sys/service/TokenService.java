package cool.houge.mahu.admin.sys.service;

import com.github.f4b6a3.ulid.UlidCreator;
import com.google.common.base.Strings;
import com.password4j.Password;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.event.AdminLoginLogEvent;
import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.admin.security.TokenVerifier;
import cool.houge.mahu.config.Status;
import cool.houge.mahu.config.TokenConfig;
import cool.houge.mahu.entity.sys.Admin;
import cool.houge.mahu.entity.sys.AdminLoginLog;
import cool.houge.mahu.model.command.TokenGrantCommand;
import cool.houge.mahu.model.result.TokenGrantResult;
import cool.houge.mahu.repository.sys.AdminRepository;
import cool.houge.mahu.repository.sys.AuthClientRepository;
import io.ebean.annotation.Transactional;
import io.helidon.common.LazyValue;
import io.helidon.security.jwt.EncryptedJwt;
import io.helidon.security.jwt.Jwt;
import io.helidon.security.jwt.JwtValidator;
import io.helidon.security.jwt.SignedJwt;
import io.helidon.security.jwt.jwk.Jwk;
import io.helidon.security.jwt.jwk.JwkKeys;
import io.helidon.service.registry.Event.Emitter;
import io.helidon.service.registry.Service.Singleton;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.random.RandomGenerator;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;

/// 令牌服务
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class TokenService implements TokenVerifier {

    private static final Logger log = LogManager.getLogger();

    private final JwkKeys jwkKeys;
    private final TokenConfig tokenConfig;
    private final AdminRepository adminRepository;
    private final Emitter<AdminLoginLogEvent> adminLoginLogEventEmitter;
    private final AuthClientRepository authClientRepository;

    @Override
    public AuthContext verify(String token) {
        var jwt = parseToken(token);
        var adminId = jwt.userPrincipal()
                .map(Integer::valueOf)
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
            public int adminId() {
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
    public TokenGrantResult token(TokenGrantCommand payload) {
        try {
            var client = authClientRepository.obtainClient(payload.getClientId());
            var admin = resolveAdmin(payload);
            ensureLoginAllowed(admin);
            var ret = makeToken(payload, admin);
            log.info("用户成功获取令牌 id={}", admin.getId());

            emitLoginLog(buildLoginLog(payload, admin.getId(), admin.getUsername(), true, null, null));
            return ret;
        } catch (LoginLogException e) {
            persistFailedLog(payload, e.adminId, e.reasonCode, e.exception.getRawMessage());
            throw e.exception;
        } catch (EntityNotFoundException e) {
            persistFailedLog(payload, null, AdminLoginLog.REASON_CLIENT_NOT_FOUND, e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            persistFailedLog(payload, null, AdminLoginLog.REASON_INTERNAL_ERROR, e.getMessage());
            throw e;
        }
    }

    TokenGrantResult makeToken(TokenGrantCommand payload, Admin admin) {
        var issueContext = createTokenIssueContext(admin);
        var accessToken = encryptToken(buildAccessJwt(payload, issueContext), issueContext.jwk);
        var refreshToken = encryptToken(buildRefreshJwt(issueContext), issueContext.jwk);

        return new TokenGrantResult()
                .setExpiresIn(tokenConfig.accessExpires().toSeconds())
                .setAccessToken(accessToken.token())
                .setRefreshToken(refreshToken.token());
    }

    private TokenIssueContext createTokenIssueContext(Admin admin) {
        return new TokenIssueContext(
                obtainJwk(),
                String.valueOf(admin.getId()),
                Instant.now(),
                UlidCreator.getUlid().toString());
    }

    private Jwt buildAccessJwt(TokenGrantCommand payload, TokenIssueContext issueContext) {
        return baseJwtBuilder(issueContext, tokenConfig.accessExpires())
                .addAudience(payload.getClientId())
                .build();
    }

    private Jwt buildRefreshJwt(TokenIssueContext issueContext) {
        return baseJwtBuilder(issueContext, tokenConfig.refreshExpires()).build();
    }

    private Jwt.Builder baseJwtBuilder(TokenIssueContext issueContext, java.time.Duration expires) {
        return Jwt.builder()
                .jwtId(UlidCreator.getMonotonicUlid().toString())
                .userPrincipal(issueContext.subject)
                .issueTime(issueContext.issueTime)
                .expirationTime(issueContext.issueTime.plus(expires))
                .nonce(issueContext.nonce);
    }

    private EncryptedJwt encryptToken(Jwt jwt, Jwk jwk) {
        return EncryptedJwt.builder(SignedJwt.sign(jwt, Jwk.NONE_JWK))
                .jwks(jwkKeys, jwk.keyId())
                .build();
    }

    @NonNull
    private Admin resolveAdmin(TokenGrantCommand payload) {
        return switch (payload.getGrantType()) {
            case PASSWORD -> loginByUsername(payload);
            case REFRESH_TOKEN -> loginByRefreshToken(payload);
            case null, default -> throw new LoginLogException(
                    BizCodes.UNIMPLEMENTED,
                    "不支持的授权类型",
                    AdminLoginLog.REASON_UNSUPPORTED_GRANT_TYPE,
                    null);
        };
    }

    private void ensureLoginAllowed(Admin admin) {
        if (Status.ACTIVE.neq(admin.getStatus())) {
            throw new LoginLogException(
                    BizCodes.PERMISSION_DENIED,
                    "该帐号禁止登录",
                    AdminLoginLog.REASON_ADMIN_DISABLED,
                    admin.getId());
        }
    }

    @NonNull
    Admin loginByUsername(TokenGrantCommand payload) {
        var user = adminRepository.findByUsername(payload.getUsername());
        if (user == null) {
            throw new LoginLogException(
                    BizCodes.NOT_FOUND,
                    Strings.lenientFormat("用户[%s]未找到", payload.getUsername()),
                    AdminLoginLog.REASON_ADMIN_NOT_FOUND,
                    null);
        }

        var checker = Password.check(payload.getPassword(), user.getPassword());
        if (!checker.withArgon2()) {
            throw new LoginLogException(
                    BizCodes.INVALID_ARGUMENT,
                    "密码不匹配",
                    AdminLoginLog.REASON_PASSWORD_MISMATCH,
                    user.getId());
        }
        return user;
    }

    @NonNull
    Admin loginByRefreshToken(TokenGrantCommand payload) {
        try {
            var jwt = parseToken(payload.getRefreshToken());
            var sub = jwt.userPrincipal().orElseThrow();
            var user = adminRepository.findById(Integer.valueOf(sub));
            if (user == null) {
                throw new LoginLogException(
                        BizCodes.NOT_FOUND,
                        Strings.lenientFormat("用户[%s]未找到", sub),
                        AdminLoginLog.REASON_ADMIN_NOT_FOUND,
                        null);
            }
            return user;
        } catch (LoginLogException e) {
            throw e;
        } catch (BizCodeException e) {
            throw new LoginLogException(
                    e,
                    e.getCode() == BizCodes.UNAUTHENTICATED
                            ? AdminLoginLog.REASON_TOKEN_EXPIRED
                            : AdminLoginLog.REASON_TOKEN_INVALID,
                    null);
        } catch (RuntimeException e) {
            throw new LoginLogException(
                    new BizCodeException(BizCodes.UNAUTHENTICATED, "刷新令牌无效", e),
                    AdminLoginLog.REASON_TOKEN_INVALID,
                    null);
        }
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

    private void persistFailedLog(TokenGrantCommand payload, Integer adminId, String reasonCode, String reasonDetail) {
        emitLoginLog(buildLoginLog(payload, adminId, payload.getUsername(), false, reasonCode, reasonDetail));
    }

    private void emitLoginLog(AdminLoginLog entity) {
        adminLoginLogEventEmitter.emit(new AdminLoginLogEvent(entity));
    }

    private AdminLoginLog buildLoginLog(
            TokenGrantCommand payload,
            Integer adminId,
            String username,
            boolean success,
            String reasonCode,
            String reasonDetail) {
        return new AdminLoginLog()
                .setAdminId(adminId)
                .setGrantType(payload.getGrantType() == null ? "UNKNOWN" : payload.getGrantType().name())
                .setClientId(payload.getClientId())
                .setUsername(username)
                .setSuccess(success)
                .setReasonCode(reasonCode)
                .setReasonDetail(reasonDetail)
                .setIpAddr(payload.getClientIp())
                .setUserAgent(payload.getUserAgent());
    }

    private static final class LoginLogException extends RuntimeException {

        private final BizCodeException exception;
        private final String reasonCode;
        private final Integer adminId;

        private LoginLogException(BizCodeException exception, String reasonCode, Integer adminId) {
            super(exception);
            this.exception = exception;
            this.reasonCode = reasonCode;
            this.adminId = adminId;
        }

        private LoginLogException(BizCodes code, String message, String reasonCode, Integer adminId) {
            this(new BizCodeException(code, message), reasonCode, adminId);
        }
    }

    private record TokenIssueContext(Jwk jwk, String subject, Instant issueTime, String nonce) {}
}

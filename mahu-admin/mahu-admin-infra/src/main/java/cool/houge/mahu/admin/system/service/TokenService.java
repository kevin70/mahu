package cool.houge.mahu.admin.system.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Strings;
import com.password4j.Password;
import cool.houge.lang.BizCodeException;
import cool.houge.lang.BizCodes;
import cool.houge.lang.HougeException;
import cool.houge.util.NanoIdUtils;
import cool.houge.mahu.admin.cache.CEmployee;
import cool.houge.mahu.admin.system.dto.TokenPayload;
import cool.houge.mahu.admin.system.dto.TokenResult;
import cool.houge.mahu.admin.system.repository.EmployeeRepository;
import cool.houge.mahu.admin.system.repository.TokenJourRepository;
import cool.houge.mahu.common.GrantType;
import cool.houge.mahu.common.security.AuthContext;
import cool.houge.mahu.common.security.TokenVerifier;
import cool.houge.mahu.config.TokenConfig;
import cool.houge.mahu.entity.system.Employee;
import cool.houge.mahu.entity.system.TokenJour;
import io.ebean.annotation.Transactional;
import io.helidon.security.jwt.EncryptedJwt;
import io.helidon.security.jwt.Jwt;
import io.helidon.security.jwt.JwtValidator;
import io.helidon.security.jwt.SignedJwt;
import io.helidon.security.jwt.jwk.Jwk;
import io.helidon.security.jwt.jwk.JwkKeys;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

import static java.util.Objects.requireNonNull;

/// 令牌服务
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class TokenService implements TokenVerifier {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    private final Cache<String, CEmployee> employeeCache = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .recordStats()
            .build();

    @Inject
    JwkKeys jwkKeys;

    @Inject
    TokenConfig config;

    @Inject
    EmployeeRepository employeeRepository;

    @Inject
    TokenJourRepository tokenJourRepository;

    @Override
    public AuthContext verify(String token) {
        var jwt = parseToken(token);
        var jwtId = jwt.jwtId().orElseThrow();
        var emp = employeeCache.get(jwtId, (k) -> {
            var uid = Long.valueOf(jwt.subject().orElseThrow());
            var entity = employeeRepository.findById(uid);
            requireNonNull(entity);

            return new CEmployee()
                    .setId(entity.getId())
                    .setNickname(entity.getNickname())
                    .setRoleCodes(List.copyOf(entity.allRolePermits()));
        });

        return new AuthContext() {

            @Override
            public long uid() {
                return emp.getId();
            }

            @Override
            public String name() {
                return emp.getNickname();
            }

            @Override
            public boolean containsPermits(String... permits) {
                var codes = rolePermits();
                if (codes.contains("*")) {
                    return true;
                }

                for (String code : permits) {
                    if (codes.contains(code)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public List<String> rolePermits() {
                return emp.getRoleCodes();
            }
        };
    }

    @Transactional
    public TokenResult token(TokenPayload payload) {
        var grantType = payload.getGrantType();

        Employee employee;
        if (grantType == GrantType.PASSWORD) {
            employee = checkByUsername(payload);
        } else if (grantType == GrantType.REFRESH_TOKEN) {
            employee = checkByRefreshToken(payload);
        } else {
            throw new BizCodeException(BizCodes.UNIMPLEMENTED);
        }

        if (employee == null) {
            throw new HougeException("登录用户未找到");
        }
        var status = employee.getStatus();
        if (status != Employee.Status.ACTIVE) {
            throw new BizCodeException(BizCodes.PERMISSION_DENIED, "该帐号禁止登录");
        }

        var ret = generateToken(employee.getId().intValue(), grantType, payload.getClientId(), payload.getClientIp());
        log.info("用户成功获取令牌 id={}", employee.getId());
        return ret;
    }

    Employee checkByUsername(TokenPayload payload) {
        var user = employeeRepository.findByUsername(payload.getUsername());
        if (user == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, Strings.lenientFormat("用户[%s]未找到", payload.getUsername()));
        }
        var checker = Password.check(payload.getPassword(), user.getPassword());
        if (!checker.withArgon2()) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "密码不匹配");
        }
        return user;
    }

    Employee checkByRefreshToken(TokenPayload payload) {
        var jwt = parseToken(payload.getRefreshToken());
        var sub = jwt.subject().orElseThrow();
        return employeeRepository.findById(Long.valueOf(sub));
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

    TokenResult generateToken(long userId, GrantType grantType, String clientId, String clientIp) {
        var jwtId = NanoIdUtils.randomNanoId();
        var sub = String.valueOf(userId);
        var iat = Instant.now();
        var jwk = obtainJwk();

        var at = generateToken(jwtId, sub, clientId, iat, iat.plus(2, ChronoUnit.DAYS), jwk);
        var rt = generateToken(jwtId, sub, clientId, iat, iat.plus(14, ChronoUnit.DAYS), jwk);

        var entity = new TokenJour()
                .setId(jwtId)
                .setCreateTime(iat)
                .setType("ADMIN")
                .setSub(sub)
                .setClientId(clientId)
                .setClientIp(clientIp)
                .setJwkId(jwk.keyId())
                .setGrantType(grantType.code)
                .setAccessToken(at)
                .setRefreshToken(rt);
        tokenJourRepository.save(entity);

        return new TokenResult().setAccessToken(at).setRefreshToken(rt).setExpiresIn(TimeUnit.DAYS.toSeconds(2));
    }

    String generateToken(String jwtId, String sub, String aud, Instant iat, Instant exp, Jwk jwk) {
        var jwt = Jwt.builder()
                .jwtId(jwtId)
                .subject(sub)
                .issueTime(iat)
                .expirationTime(exp)
                .addAudience(aud)
                .nonce(NanoIdUtils.randomNanoId())
                .build();
        var signedJwt = SignedJwt.sign(jwt, Jwk.NONE_JWK);
        var encryptedJwt =
                EncryptedJwt.builder(signedJwt).jwks(jwkKeys, jwk.keyId()).build();
        return encryptedJwt.token();
    }

    Jwk obtainJwk() {
        var keys = jwkKeys.keys();
        var ran = RandomGenerator.getDefault();
        var i = ran.nextInt(keys.size());
        return keys.get(i);
    }
}

package cool.houge.mahu.admin.system.service;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.google.common.base.Strings;
import com.password4j.Password;
import cool.houge.mahu.admin.DynamicPermit;
import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.admin.security.TokenVerifier;
import cool.houge.mahu.admin.system.dto.TokenPayload;
import cool.houge.mahu.admin.system.dto.TokenResult;
import cool.houge.mahu.admin.system.repository.EmployeeRepository;
import cool.houge.mahu.admin.system.repository.TokenJourRepository;
import cool.houge.mahu.common.BizCodeException;
import cool.houge.mahu.common.BizCodes;
import cool.houge.mahu.common.GrantType;
import cool.houge.mahu.config.TokenConfig;
import cool.houge.mahu.entity.market.Shop;
import cool.houge.mahu.entity.system.Employee;
import io.ebean.annotation.Transactional;
import io.helidon.common.LazyValue;
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
import java.util.List;
import java.util.random.RandomGenerator;

/// 令牌服务
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class TokenService implements TokenVerifier {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    @Inject
    JwkKeys jwkKeys;

    @Inject
    TokenConfig tokenConfig;

    @Inject
    EmployeeRepository employeeRepository;

    @Inject
    TokenJourRepository tokenJourRepository;

    @Override
    public AuthContext verify(String token) {
        var jwt = parseToken(token);
        var employeeId = jwt.userPrincipal()
                .map(Long::valueOf)
                .orElseThrow(() -> new BizCodeException(BizCodes.UNAUTHENTICATED, "非法的访问令牌"));

        var employeeLv = LazyValue.create(() -> {
            var emp = employeeRepository.findById(employeeId);
            if (emp == null) {
                throw new BizCodeException(BizCodes.UNAUTHENTICATED, "未找到管理员");
            }
            return emp;
        });

        return new AuthContext() {

            @Override
            public long uid() {
                return employeeId;
            }

            @Override
            public String name() {
                return employeeLv.get().getNickname();
            }

            @Override
            public boolean checkPermit(Object object) {
                var codes = permits();
                // 拥有超级管理员权限
                if (codes.contains("*")) {
                    return true;
                }

                // 拥有指定的权限代码
                if (object instanceof String p) {
                    if (codes.contains(p)) {
                        return true;
                    }
                }

                if (object instanceof DynamicPermit p) {
                    // 用户拥有操作指定商店数据的权限
                    if (DynamicPermit.KIND_SHOP.equals(p.kind())) {
                        var shopId = p.parameters().first("shop_id").asInt().get();
                        var shopIds = shopIds();
                        return shopIds.contains(shopId);
                    }
                }

                return false;
            }

            @Override
            public List<String> permits() {
                return List.copyOf(employeeLv.get().allRolePermits());
            }

            @Override
            public List<Integer> shopIds() {
                return employeeLv.get().getShops().stream().map(Shop::getId).toList();
            }
        };
    }

    @Transactional
    public TokenResult token(TokenPayload payload) {
        var grantType = payload.getGrantType();

        Employee employee;
        if (grantType == GrantType.PASSWORD) {
            employee = loginByUsername(payload);
        } else if (grantType == GrantType.REFRESH_TOKEN) {
            employee = loginByRefreshToken(payload);
        } else {
            throw new BizCodeException(BizCodes.UNIMPLEMENTED);
        }

        if (employee == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, "登录用户未找到");
        }

        var status = employee.getStatus();
        if (status != Employee.Status.ACTIVE) {
            throw new BizCodeException(BizCodes.PERMISSION_DENIED, "该帐号禁止登录");
        }
        var ret = makeToken(payload, employee);
        log.info("用户成功获取令牌 id={}", employee.getId());
        return ret;
    }

    @Transactional
    TokenResult makeToken(TokenPayload payload, Employee employee) {
        var jwk = obtainJwk();
        var jwtId = UlidCreator.getUlid().toLowerCase();
        var sub = String.valueOf(employee.getId());
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

        return new TokenResult()
                .setExpiresIn(tokenConfig.accessExpires().toSeconds())
                .setAccessToken(accessToken.token())
                .setRefreshToken(refreshToken.token());
    }

    Employee loginByUsername(TokenPayload payload) {
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

    Employee loginByRefreshToken(TokenPayload payload) {
        var jwt = parseToken(payload.getRefreshToken());
        var sub = jwt.userPrincipal().orElseThrow();
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

    Jwk obtainJwk() {
        var keys = jwkKeys.keys();
        var ran = RandomGenerator.getDefault();
        var i = ran.nextInt(keys.size());
        return keys.get(i);
    }
}

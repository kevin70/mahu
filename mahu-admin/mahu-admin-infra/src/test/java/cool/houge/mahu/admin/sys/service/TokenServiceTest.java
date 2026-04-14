package cool.houge.mahu.admin.sys.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import cool.houge.mahu.entity.sys.Admin;
import cool.houge.mahu.model.command.TokenGrantCommand;
import cool.houge.mahu.repository.sys.AdminRepository;
import cool.houge.mahu.repository.sys.AuthClientRepository;
import io.helidon.security.jwt.Jwt;
import io.helidon.service.registry.Services;
import io.helidon.testing.junit5.Testing;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Testing.Test(perMethod = true)
class TokenServiceTest {

    private static final String CLIENT_ID = "mahu-admin-web";
    private static final Duration ACCESS_EXPIRES = Duration.ofHours(24);
    private static final Duration REFRESH_EXPIRES = Duration.ofDays(14);

    private final AdminRepository adminRepository = mock(AdminRepository.class);
    private final AuthClientRepository authClientRepository = mock(AuthClientRepository.class);

    @BeforeEach
    void setUp() {
        Services.set(AdminRepository.class, adminRepository);
        Services.set(AuthClientRepository.class, authClientRepository);
    }

    @Test
    void makeToken_生成访问与刷新令牌(TokenService tokenService) {
        var result = tokenService.makeToken(payload(), admin());

        assertNotNull(result.getAccessToken());
        assertNotNull(result.getRefreshToken());
        assertEquals(ACCESS_EXPIRES.toSeconds(), result.getExpiresIn());
    }

    @Test
    void makeToken_访问与刷新令牌声明符合预期(TokenService tokenService) {
        var result = tokenService.makeToken(payload(), admin());
        var accessJwt = tokenService.parseToken(result.getAccessToken());
        var refreshJwt = tokenService.parseToken(result.getRefreshToken());

        assertEquals("1001", accessJwt.userPrincipal().orElseThrow());
        assertEquals("1001", refreshJwt.userPrincipal().orElseThrow());
        assertFalse(accessJwt.nonce().orElseThrow().isBlank());
        assertEquals(accessJwt.nonce().orElseThrow(), refreshJwt.nonce().orElseThrow());
        assertEquals(ACCESS_EXPIRES, tokenLifetime(accessJwt));
        assertEquals(REFRESH_EXPIRES, tokenLifetime(refreshJwt));
    }

    @Test
    void makeToken_访问与刷新令牌使用不同jwtId(TokenService tokenService) {
        var result = tokenService.makeToken(payload(), admin());
        var accessJwt = tokenService.parseToken(result.getAccessToken());
        var refreshJwt = tokenService.parseToken(result.getRefreshToken());

        assertNotEquals(accessJwt.jwtId().orElseThrow(), refreshJwt.jwtId().orElseThrow());
    }

    @Test
    void makeToken_访问令牌包含客户端audience_刷新令牌不包含(TokenService tokenService) {
        var result = tokenService.makeToken(payload(), admin());
        var accessJwt = tokenService.parseToken(result.getAccessToken());
        var refreshJwt = tokenService.parseToken(result.getRefreshToken());

        assertEquals(List.of(CLIENT_ID), accessJwt.audience().orElseThrow());
        assertTrue(refreshJwt.audience().isEmpty());
    }

    private Duration tokenLifetime(Jwt jwt) {
        return Duration.between(jwt.issueTime().orElseThrow(), jwt.expirationTime().orElseThrow());
    }

    private TokenGrantCommand payload() {
        var payload = new TokenGrantCommand();
        payload.setClientId(CLIENT_ID);
        return payload;
    }

    private Admin admin() {
        return new Admin().setId(1001);
    }
}

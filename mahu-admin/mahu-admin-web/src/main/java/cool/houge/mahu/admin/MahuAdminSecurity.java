package cool.houge.mahu.admin;

import static io.helidon.http.HeaderNames.AUTHORIZATION;

import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.admin.security.TokenVerifier;
import io.helidon.http.ForbiddenException;
import io.helidon.http.UnauthorizedException;
import io.helidon.security.jwt.JwtException;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.HttpSecurity;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/// 安全认证
///
/// @author ZY (kzou227@qq.com)
@Singleton
public record MahuAdminSecurity(TokenVerifier tokenVerifier) implements HttpSecurity {

    private static final String SCHEME_PREFIX = "Bearer ";

    @Override
    public boolean authenticate(ServerRequest request, ServerResponse response, boolean requiredHint)
            throws UnauthorizedException {
        Supplier<Optional<String>> headerGet = () -> request.headers()
                .first(AUTHORIZATION)
                .filter(s -> s.length() > SCHEME_PREFIX.length())
                .map(s -> s.substring(SCHEME_PREFIX.length()));
        var accessTokenOpt = request.query()
                .first("access_token")
                .or(headerGet)
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty));
        if (accessTokenOpt.isEmpty()) {
            throw new UnauthorizedException("缺少访问令牌");
        }

        try {
            // 校验访问令牌
            var ac = tokenVerifier.verify(accessTokenOpt.get());
            request.context().register(ac);
            return true;
        } catch (JwtException e) {
            throw new UnauthorizedException("认证失败", e);
        }
    }

    @Override
    public boolean authorize(ServerRequest request, ServerResponse response, String... roleHint)
            throws ForbiddenException {
        if (roleHint.length != 0) {
            var ac = AuthContext.current();
            for (String s : roleHint) {
                if (ac.hasPermission(s)) {
                    return true;
                }
            }
        }
        throw new ForbiddenException("没有访问权限");
    }
}

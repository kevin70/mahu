package cool.houge.mahu;

import com.google.common.base.Strings;
import cool.houge.mahu.common.security.AuthContext;
import cool.houge.mahu.common.security.TokenVerifier;
import io.helidon.http.ForbiddenException;
import io.helidon.http.HeaderNames;
import io.helidon.http.UnauthorizedException;
import io.helidon.security.jwt.JwtException;
import io.helidon.webserver.http.HttpSecurity;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 安全认证
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class MahuSecurity implements HttpSecurity {

    @Inject
    TokenVerifier tokenVerifier;

    @Override
    public boolean authenticate(ServerRequest request, ServerResponse response, boolean requiredHint)
            throws UnauthorizedException {
        String accessToken;
        var accessTokenOptional = request.query().first("access_token");
        if (accessTokenOptional.isPresent()) {
            accessToken = accessTokenOptional.get();
        } else {
            accessToken = request.headers()
                    .first(HeaderNames.AUTHORIZATION)
                    .map(s -> s.replaceFirst("Bearer ", ""))
                    .orElse(null);
        }

        if (Strings.isNullOrEmpty(accessToken)) {
            throw new UnauthorizedException("未找到访问令牌");
        }

        try {
            // 校验访问令牌
            var ac = tokenVerifier.verify(accessToken);
            request.context().register(ac);
            return true;
        } catch (JwtException e) {
            throw new UnauthorizedException("认证失败", e);
        }
    }

    @Override
    public boolean authorize(ServerRequest request, ServerResponse response, String... roleHint)
            throws ForbiddenException {
        if (roleHint.length == 0) {
            return true;
        }

        var opt = request.context().get(AuthContext.class);
        if (opt.isEmpty()) {
            throw new UnauthorizedException("缺少 AuthContext");
        }

        var ac = opt.get();
        ac.validatePermit(roleHint);
        return true;
    }
}

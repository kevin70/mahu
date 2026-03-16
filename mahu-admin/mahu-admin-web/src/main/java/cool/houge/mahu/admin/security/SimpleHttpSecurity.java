package cool.houge.mahu.admin.security;

import io.helidon.http.ForbiddenException;
import io.helidon.http.UnauthorizedException;
import io.helidon.webserver.http.HttpSecurity;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

/// 默认的 HttpSecurity 实现，基于 AuthContext 进行认证与授权。
public class SimpleHttpSecurity implements HttpSecurity {

    @Override
    public boolean authenticate(ServerRequest request, ServerResponse response, boolean requiredHint)
            throws UnauthorizedException {
        obtainAuthContext(request);
        return true;
    }

    @Override
    public boolean authorize(ServerRequest request, ServerResponse response, String... roleHint)
            throws ForbiddenException {
        if (roleHint.length != 0) {
            var ac = obtainAuthContext(request);
            for (String s : roleHint) {
                if (ac.hasPermission(s)) {
                    return true;
                }
            }
        }
        throw new ForbiddenException("没有访问权限");
    }

    AuthContext obtainAuthContext(ServerRequest request) {
        var ac = request.context().get(AuthContext.class);
        if (ac.isEmpty() || ac.get() == AuthContext.ANONYMOUS) {
            throw new UnauthorizedException("缺少认证");
        }
        return ac.get();
    }
}

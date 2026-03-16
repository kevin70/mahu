package cool.houge.mahu.admin.web.filter;

import cool.houge.mahu.admin.event.AdminAccessEvent;
import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.entity.sys.AdminAccessLog;
import cool.houge.mahu.util.Metadata;
import io.helidon.http.HeaderNames;
import io.helidon.http.Method;
import io.helidon.service.registry.Event;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.http.Filter;
import io.helidon.webserver.http.FilterChain;
import io.helidon.webserver.http.RoutingRequest;
import io.helidon.webserver.http.RoutingResponse;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 管理员访问日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class AdminAccessLogFilter implements Filter {

    private static final Logger log = LogManager.getLogger(AdminAccessLogFilter.class);
    final Event.Emitter<AdminAccessEvent> adminAccessEventEmitter;

    @Override
    public void filter(FilterChain chain, RoutingRequest req, RoutingResponse res) {
        res.whenSent(() -> {
            try {
                // 记录后台访问日志
                saveAccessLog(req, res);
            } catch (Exception e) {
                log.error("记录访问日志出现异常", e);
            }
        });
        chain.proceed();
    }

    void saveAccessLog(ServerRequest req, ServerResponse res) {
        var prologue = req.prologue();
        if (prologue.method() == Method.OPTIONS || prologue.method() == Method.HEAD) {
            return;
        }
        // 不需要认证的接口不记录日志
        var ctx = req.context();
        var authContextOpt = ctx.get(AuthContext.class);
        if (authContextOpt.isEmpty() || authContextOpt.get() == AuthContext.ANONYMOUS) {
            return;
        }

        var routedPath = req.path().rawPath();
        // 访问日志不记录日志
        if (prologue.method() == Method.GET && "/system/access-logs".equals(routedPath)) {
            return;
        }
        // 获取个人信息不记录日志
        if (prologue.method() == Method.GET && "/me/profile".equals(routedPath)) {
            return;
        }

        var metadata = Metadata.current();
        var accessLog = new AdminAccessLog()
                .setAdminId(authContextOpt.get().adminId())
                .setIpAddr(metadata.clientAddr())
                .setUserAgent(metadata.userAgent())
                .setMethod(prologue.method().text())
                .setUriPath(prologue.uriPath().rawPath())
                .setUriQuery(prologue.query().rawValue())
                .setProtocol(prologue.rawProtocol())
                .setResponseStatus(res.status().code())
                .setResponseBytes(res.bytesWritten());

        var headers = req.headers();
        headers.first(HeaderNames.REFERER).ifPresent(accessLog::setReferer);
        adminAccessEventEmitter.emit(new AdminAccessEvent(accessLog));
    }
}

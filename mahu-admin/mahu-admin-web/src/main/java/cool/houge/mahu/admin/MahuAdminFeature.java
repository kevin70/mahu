package cool.houge.mahu.admin;

import com.google.common.base.Splitter;
import cool.houge.mahu.admin.entity.AdminAccessLog;
import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.common.Metadata;
import io.avaje.inject.events.Event;
import io.helidon.common.configurable.Resource;
import io.helidon.common.media.type.MediaTypes;
import io.helidon.http.HeaderName;
import io.helidon.http.HeaderNames;
import io.helidon.http.Method;
import io.helidon.webserver.http.*;
import io.hypersistence.tsid.TSID;
import jakarta.inject.Singleton;

import java.util.List;

import static io.helidon.http.HeaderNames.X_FORWARDED_FOR;

/// 功能注册
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class MahuAdminFeature implements HttpFeature, Filter {

    private static final HeaderName X_REQUEST_ID = HeaderNames.create("x-request-id");

    private final MahuAdminSecurity security;
    private final List<HttpService> httpServices;
    private final Event<AdminAccessLog> accessLogEvent;

    public MahuAdminFeature(
            MahuAdminSecurity security, List<HttpService> httpServices, Event<AdminAccessLog> accessLogEvent) {
        this.security = security;
        this.httpServices = httpServices;
        this.accessLogEvent = accessLogEvent;
    }

    @Override
    public void setup(HttpRouting.Builder routing) {
        routing.addFilter(this).security(security).addFeature(new MahuAdminErrorFeature());

        // 注册 HTTP 服务
        for (HttpService httpService : httpServices) {
            routing.register(httpService);
        }

        routing.get("/openapi/ui", (req, res) -> {
            // 输出接口文档界面
            res.header(HeaderNames.CONTENT_TYPE, MediaTypes.TEXT_HTML.type())
                    .send(Resource.create("META-INF/openapi-ui.html").bytes());
        });
    }

    @Override
    public void filter(FilterChain chain, RoutingRequest req, RoutingResponse res) {
        // 设置请求访问元数据
        req.context().supply(Metadata.class, () -> new Metadata() {

            @Override
            public String clientAddr() {
                return req.headers()
                        .first(X_FORWARDED_FOR)
                        .map(s -> {
                            var splitter = Splitter.on(',').omitEmptyStrings().trimResults();
                            var ipList = splitter.splitToList(s);
                            return ipList.getFirst();
                        })
                        .orElseGet(() -> req.remotePeer().host());
            }

            @Override
            public String traceId() {
                return req.headers().first(X_REQUEST_ID).orElseGet(() -> TSID.fast()
                        .toString());
            }
        });

        // 记录后台访问日志
        res.whenSent(() -> fireAccessLog(req, res));
        chain.proceed();
    }

    void fireAccessLog(ServerRequest req, ServerResponse res) {
        var prologue = req.prologue();
        var routedPath = req.path().rawPath();
        // 访问日志不记录日志
        if (prologue.method() == Method.GET && "/system/access-logs".equals(routedPath)) {
            return;
        }
        // 获取个人信息不记录日志
        if (prologue.method() == Method.GET && "/me/profile".equals(routedPath)) {
            return;
        }

        // 不需要认证的接口不记录日志
        var ctx = req.context();
        var authContextOpt = ctx.get(AuthContext.class);
        if (authContextOpt.isEmpty()) {
            return;
        }

        var metadata = ctx.get(Metadata.class).orElseThrow();
        var accessLog = new AdminAccessLog()
                .setAdminId(authContextOpt.get().uid())
                .setIpAddr(metadata.clientAddr())
                .setMethod(prologue.method().text())
                .setUriPath(prologue.uriPath().rawPath())
                .setUriQuery(prologue.query().rawValue())
                .setProtocol(prologue.rawProtocol())
                .setResponseStatus(res.status().code())
                .setResponseBytes(res.bytesWritten());

        var headers = req.headers();
        headers.first(HeaderNames.REFERER).ifPresent(accessLog::setReferer);
        headers.first(HeaderNames.USER_AGENT).ifPresent(accessLog::setUserAgent);
        accessLogEvent.fire(accessLog);
    }
}

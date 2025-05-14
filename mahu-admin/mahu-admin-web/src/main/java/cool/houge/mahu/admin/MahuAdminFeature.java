package cool.houge.mahu.admin;

import static io.helidon.http.HeaderNames.USER_AGENT;
import static io.helidon.http.HeaderNames.X_FORWARDED_FOR;

import com.google.common.base.Splitter;
import cool.houge.mahu.TraceIdGenerator;
import cool.houge.mahu.admin.entity.AdminAccessLog;
import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.admin.shared.SharedService;
import cool.houge.mahu.common.Metadata;
import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.common.configurable.Resource;
import io.helidon.common.media.type.MediaTypes;
import io.helidon.http.HeaderName;
import io.helidon.http.HeaderNames;
import io.helidon.http.Method;
import io.helidon.logging.common.HelidonMdc;
import io.helidon.webserver.http.Filter;
import io.helidon.webserver.http.FilterChain;
import io.helidon.webserver.http.HttpFeature;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.RoutingRequest;
import io.helidon.webserver.http.RoutingResponse;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.inject.Singleton;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 功能注册
///
/// @author ZY (kzou227@qq.com)
@Singleton
@Weight(Weighted.DEFAULT_WEIGHT + 100)
public class MahuAdminFeature implements HttpFeature, Filter {

    private static final Logger log = LogManager.getLogger(MahuAdminFeature.class);
    private static final HeaderName X_REQUEST_ID = HeaderNames.create("x-request-id");

    private final MahuAdminSecurity security;
    private final List<HttpService> httpServices;
    private final SharedService sharedService;

    public MahuAdminFeature(MahuAdminSecurity security, List<HttpService> httpServices, SharedService sharedService) {
        this.security = security;
        this.httpServices = httpServices;
        this.sharedService = sharedService;
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
        var traceId = req.headers().first(X_REQUEST_ID).orElseGet(TraceIdGenerator::generate);
        HelidonMdc.set("MAHU_TRACE_ID", traceId);

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
            public String userAgent() {
                return req.headers().first(USER_AGENT).orElse("UNKNOWN");
            }

            @Override
            public String traceId() {
                return traceId;
            }
        });

        res.whenSent(() -> {
            try {
                // 记录后台访问日志
                saveAccessLog(req, res);
            } catch (Exception e) {
                log.error("记录访问日志出现异常", e);
            }
        });

        chain.proceed();
        // 清理追踪 ID
        res.whenSent(() -> HelidonMdc.remove("MAHU_TRACE_ID"));
    }

    void saveAccessLog(ServerRequest req, ServerResponse res) {
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

        var metadata = Metadata.metadata();
        var accessLog = new AdminAccessLog()
                .setAdminId(authContextOpt.get().uid())
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
        sharedService.save(accessLog);
    }
}

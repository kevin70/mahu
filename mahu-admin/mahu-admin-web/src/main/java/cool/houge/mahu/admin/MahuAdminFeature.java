package cool.houge.mahu.admin;

import cool.houge.mahu.admin.entity.AdminAccessLog;
import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.util.Metadata;
import cool.houge.mahu.web.WebMetadata;
import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.http.HeaderNames;
import io.helidon.http.Method;
import io.helidon.logging.common.HelidonMdc;
import io.helidon.service.registry.Service;
import io.helidon.webserver.http.Filter;
import io.helidon.webserver.http.FilterChain;
import io.helidon.webserver.http.HttpFeature;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.RoutingRequest;
import io.helidon.webserver.http.RoutingResponse;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 功能注册
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@Weight(Weighted.DEFAULT_WEIGHT + 100)
public class MahuAdminFeature implements HttpFeature, Filter {

    private static final Logger log = LogManager.getLogger(MahuAdminFeature.class);

    private final MahuAdminSecurity security;
    private final List<HttpService> httpServices;

    public MahuAdminFeature(MahuAdminSecurity security, List<HttpService> httpServices) {
        this.security = security;
        this.httpServices = httpServices;
    }

    @Override
    public void setup(HttpRouting.Builder routing) {
        routing.addFilter(this).security(security).addFeature(new MahuAdminErrorFeature());

        // 注册 HTTP 服务
        for (HttpService httpService : httpServices) {
            routing.register(httpService);
        }
    }

    @Override
    public void filter(FilterChain chain, RoutingRequest req, RoutingResponse res) {
        var metadata = new WebMetadata(req);
        HelidonMdc.set("traceId", metadata.traceId());
        // 设置请求访问元数据
        req.context().supply(Metadata.class, () -> metadata);

        res.whenSent(() -> {
            try {
                // 记录后台访问日志
                saveAccessLog(req, res);
            } catch (Exception e) {
                log.error("记录访问日志出现异常", e);
            }
        });

        // 清理追踪 ID
        res.whenSent(() -> HelidonMdc.remove("traceId"));
        chain.proceed();
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

        var metadata = Metadata.current();
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
        // sharedService.save(accessLog);
    }
}

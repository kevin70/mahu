package cool.houge.mahu.admin;

import cool.houge.mahu.util.Metadata;
import cool.houge.mahu.web.WebMetadata;
import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.logging.common.HelidonMdc;
import io.helidon.service.registry.Service;
import io.helidon.webserver.http.Filter;
import io.helidon.webserver.http.FilterChain;
import io.helidon.webserver.http.HttpFeature;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.RoutingRequest;
import io.helidon.webserver.http.RoutingResponse;
import java.util.List;

/// 应用 HTTP 功能注册
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@Weight(Weighted.DEFAULT_WEIGHT + 5)
public class AppHttpFeature implements HttpFeature, Filter {

    private final SimpleSecurity security;
    private final List<HttpService> httpServices;

    public AppHttpFeature(SimpleSecurity security, List<HttpService> httpServices) {
        this.security = security;
        this.httpServices = httpServices;
    }

    @Override
    public void setup(HttpRouting.Builder routing) {
        routing.addFilter(this).security(security);

        // 注册 HTTP 服务
        for (HttpService httpService : httpServices) {
            routing.register(httpService);
        }
    }

    @Override
    public void filter(FilterChain chain, RoutingRequest req, RoutingResponse res) {
        var metadata = new WebMetadata(req);
        try {
            HelidonMdc.set("traceId", metadata.traceId());
            req.context().supply(Metadata.class, () -> metadata);
            chain.proceed();
        } finally {
            // 清理追踪 ID
            HelidonMdc.remove("traceId");
        }
    }
}

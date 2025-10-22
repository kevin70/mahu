package cool.houge.mahu.admin;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.codec.base.Base58BtcCodec;
import cool.houge.mahu.util.Metadata;
import cool.houge.mahu.web.WebMetadata;
import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.http.HeaderName;
import io.helidon.http.HeaderNames;
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
import java.util.List;

/// 应用 HTTP 功能注册
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@Weight(Weighted.DEFAULT_WEIGHT + 5)
public class AppHttpFeature implements HttpFeature, Filter {

    private static final HeaderName X_REQUEST_ID = HeaderNames.create("x-request-id");

    private final SimpleSecurity security;
    private final List<HttpService> httpServices;
    private final List<Filter> filters;

    public AppHttpFeature(SimpleSecurity security, List<HttpService> httpServices, List<Filter> filters) {
        this.security = security;
        this.httpServices = httpServices;
        this.filters = filters;
    }

    @Override
    public void setup(HttpRouting.Builder routing) {
        routing.addFilter(this).security(security);
        for (Filter filter : filters) {
            routing.addFilter(filter);
        }

        // 注册 HTTP 服务
        for (HttpService httpService : httpServices) {
            routing.register(httpService);
        }
    }

    @Override
    public void filter(FilterChain chain, RoutingRequest request, RoutingResponse response) {
        var traceId = traceId(request);
        var metadata = new WebMetadata(request, traceId);
        try {
            HelidonMdc.set("traceId", traceId);
            request.context().supply(Metadata.class, () -> metadata);
            chain.proceed();
        } finally {
            // 清理追踪 ID
            HelidonMdc.remove("traceId");
            response.header(X_REQUEST_ID, traceId);
        }
    }

    private String traceId(ServerRequest request) {
        return request.headers()
                .first(X_REQUEST_ID)
                .orElseGet(() -> Base58BtcCodec.INSTANCE.encode(UuidCreator.getTimeOrderedEpoch()));
    }
}

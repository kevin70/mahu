package cool.houge.mahu;

import cool.houge.mahu.common.Metadata;
import cool.houge.mahu.web.WebMetadata;
import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.logging.common.HelidonMdc;
import io.helidon.webserver.http.Filter;
import io.helidon.webserver.http.FilterChain;
import io.helidon.webserver.http.HttpFeature;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.RoutingRequest;
import io.helidon.webserver.http.RoutingResponse;
import jakarta.inject.Singleton;
import java.util.List;

/// 功能注册
///
/// @author ZY (kzou227@qq.com)
@Singleton
@Weight(Weighted.DEFAULT_WEIGHT + 100)
public class MahuFeature implements HttpFeature, Filter {

    private final List<HttpService> httpServices;
    private final MahuSecurity security;

    public MahuFeature(List<HttpService> httpServices, MahuSecurity security) {
        this.httpServices = httpServices;
        this.security = security;
    }

    @Override
    public void setup(HttpRouting.Builder routing) {
        routing.addFilter(this).addFeature(new MahuErrorFeature()).security(security);

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
        req.context().register(Metadata.class, metadata);

        // 清理追踪 ID
        res.whenSent(() -> HelidonMdc.remove("traceId"));
        chain.proceed();
    }
}

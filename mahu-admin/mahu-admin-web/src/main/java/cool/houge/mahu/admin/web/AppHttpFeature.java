package cool.houge.mahu.admin.web;

import cool.houge.mahu.admin.security.SimpleHttpSecurity;
import cool.houge.mahu.web.filter.AppRequestContextFilter;
import cool.houge.mahu.web.problem.RestErrorHandler;
import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.service.registry.Service;
import io.helidon.webserver.http.Filter;
import io.helidon.webserver.http.HttpFeature;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.http.HttpService;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;

/// 管理端 Web 应用的 HTTP 装配入口（异常处理、安全配置、全局 Filter 与 HttpService 注册）。
@Service.Singleton
@AllArgsConstructor
@Weight(Weighted.DEFAULT_WEIGHT + 5)
public class AppHttpFeature implements HttpFeature {

    private final List<HttpService> httpServices;
    private final List<Filter> filters;

    @Override
    public void setup(HttpRouting.Builder routing) {
        registerGlobalErrorHandlingAndSecurity(routing);
        registerGlobalFilters(routing);
        registerHttpServices(routing);
    }

    private void registerGlobalErrorHandlingAndSecurity(HttpRouting.Builder routing) {
        routing.error(Throwable.class, new RestErrorHandler()).security(new SimpleHttpSecurity());
    }

    private void registerGlobalFilters(HttpRouting.Builder routing) {
        routing.addFilter(new AppRequestContextFilter(Set.of(1)));
        for (Filter filter : filters) {
            routing.addFilter(filter);
        }
    }

    private void registerHttpServices(HttpRouting.Builder routing) {
        for (HttpService httpService : httpServices) {
            routing.register(httpService);
        }
    }
}

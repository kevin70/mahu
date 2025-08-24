package cool.houge.mahu.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import cool.houge.mahu.config.ConfigKeys;
import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.config.Config;
import io.helidon.health.HealthCheck;
import io.helidon.http.media.MediaContext;
import io.helidon.http.media.MediaContextConfig;
import io.helidon.http.media.jackson.JacksonSupport;
import io.helidon.service.registry.Service;
import io.helidon.service.registry.Service.RunLevel;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.WebServerConfig;
import io.helidon.webserver.context.ContextFeature;
import io.helidon.webserver.cors.CorsFeature;
import io.helidon.webserver.http.HttpFeature;
import io.helidon.webserver.observe.ObserveFeature;
import io.helidon.webserver.observe.config.ConfigObserver;
import io.helidon.webserver.observe.health.HealthObserver;
import io.helidon.webserver.observe.metrics.MetricsObserver;
import java.util.List;
import java.util.function.Supplier;

/// 应用启动
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@RunLevel(RunLevel.SERVER)
@Weight(Weighted.DEFAULT_WEIGHT - 9999)
class WebServerProvider implements Supplier<WebServer> {

    private final WebServer webServer;

    WebServerProvider(Config root, List<HttpFeature> httpFeatures, List<HealthCheck> healthChecks) {
        var observeFeature = ObserveFeature.just(
                HealthObserver.create(b -> {
                    b.details(true);
                    for (HealthCheck healthCheck : healthChecks) {
                        b.addCheck(healthCheck);
                    }
                }),
                MetricsObserver.create(),
                ConfigObserver.create());

        this.webServer = WebServerConfig.builder()
                .config(root.get(ConfigKeys.SERVER))
                .shutdownHook(false)
                .mediaContext(mediaContext())
                .addFeature(ContextFeature.create())
                .addFeature(CorsFeature.create(root))
                .addFeature(observeFeature)
                .routing(builder -> {
                    for (HttpFeature httpFeature : httpFeatures) {
                        builder.addFeature(httpFeature);
                    }
                })
                .build();
    }

    @Override
    public WebServer get() {
        return webServer;
    }

    @Service.PostConstruct
    void init() {
        webServer.start();
    }

    @Service.PreDestroy
    void destroy() {
        webServer.stop();
    }

    private MediaContext mediaContext() {
        return MediaContextConfig.builder()
                .addMediaSupport(JacksonSupport.create(objectMapper()))
                .build();
    }

    private ObjectMapper objectMapper() {
        return new ObjectMapper()
                .findAndRegisterModules()
                .setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}

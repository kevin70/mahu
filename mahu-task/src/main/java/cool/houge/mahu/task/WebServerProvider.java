package cool.houge.mahu.task;

import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.config.Config;
import io.helidon.service.registry.Service;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.WebServerConfig;
import java.util.function.Supplier;

/// 应用启动
///
/// @author ZY (kzou227@qq.com)
@Singleton
@Weight(Weighted.DEFAULT_WEIGHT - 9999)
public class WebServerProvider implements Supplier<WebServer> {

    private final WebServer webServer;

    public WebServerProvider(Config config) {
        this.webServer = WebServerConfig.builder()
                .config(config.get("server"))
                .shutdownHook(false)
                // .routing(builder -> {
                //     for (HttpFeature httpFeature : httpFeatures) {
                //         builder.addFeature(httpFeature);
                //     }
                // })
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
}

package cool.houge.mahu;

import static io.helidon.Main.addShutdownHandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Stopwatch;
import cool.houge.mahu.common.WeightedShutdownHandler;
import cool.houge.mahu.common.health.DatabaseHealthCheck;
import io.avaje.inject.BeanScope;
import io.ebean.Database;
import io.helidon.Main;
import io.helidon.config.Config;
import io.helidon.http.media.MediaContext;
import io.helidon.http.media.MediaContextConfig;
import io.helidon.http.media.jackson.JacksonSupport;
import io.helidon.spi.HelidonStartupProvider;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.WebServerConfig;
import io.helidon.webserver.context.ContextFeature;
import io.helidon.webserver.cors.CorsFeature;
import io.helidon.webserver.http.HttpFeature;
import io.helidon.webserver.observe.ObserveFeature;
import io.helidon.webserver.observe.health.HealthObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 启动应用程序
///
/// @author ZY (kzou227@qq.com)
public class MahuStartup implements HelidonStartupProvider {

    private static final Logger log = LogManager.getLogger();

    @Override
    public void start(String[] args) {
        var stopWatch = Stopwatch.createStarted();
        System.setProperty("ebean.registerShutdownHook", "false");

        var beanScore = this.startIoC();
        var webServer = this.startWeb(beanScore);

        // 优先停止 WEB 服务
        addShutdownHandler(new WeightedShutdownHandler(webServer::stop, 0));
        addShutdownHandler(new WeightedShutdownHandler(beanScore::close, -9999));
        stopWatch.stop();
        log.info("应用启动完成，耗时 {}", stopWatch);
    }

    private BeanScope startIoC() {
        return BeanScope.builder()
                .bean(Config.class, Config.create())
                .shutdownHook(false)
                .build();
    }

    private WebServer startWeb(BeanScope beanScope) {
        var config = beanScope.get(Config.class);
        var httpFeatures = beanScope.listByPriority(HttpFeature.class);
        var observeFeature = ObserveFeature.just(HealthObserver.create(b -> {
            b.details(true);
            b.addChecks(new DatabaseHealthCheck(beanScope.get(Database.class)));
        }));

        var webServer = WebServerConfig.builder()
                .config(config.get("server"))
                .shutdownHook(false)
                .mediaContext(mediaContext())
                .addFeature(CorsFeature.create(config))
                .addFeature(ContextFeature.create())
                .addFeature(observeFeature)
                .routing(builder -> {
                    for (HttpFeature httpFeature : httpFeatures) {
                        builder.addFeature(httpFeature);
                    }
                })
                .build();
        webServer.start();
        return webServer;
    }

    private MediaContext mediaContext() {
        return MediaContextConfig.builder()
                .addMediaSupport(JacksonSupport.create(objectMapper()))
                .build();
    }

    private ObjectMapper objectMapper() {
        return new ObjectMapper()
                .findAndRegisterModules()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static void main(String[] args) {
        Main.main(args);
    }
}

package cool.houge.mahu;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.helidon.common.config.Config;
import io.helidon.http.media.MediaContext;
import io.helidon.http.media.MediaContextConfig;
import io.helidon.http.media.jackson.JacksonSupport;
import io.helidon.openapi.OpenApiFeature;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.WebServerConfig;
import io.helidon.webserver.context.ContextFeature;
import io.helidon.webserver.cors.CorsFeature;
import io.helidon.webserver.http.HttpFeature;

import java.util.List;

/// Helidon 配置
///
/// @author ZY (kzou227@qq.com)
@Factory
public class WebServerBeanFactory {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WebServer webServer(Config config, List<HttpFeature> httpFeatures) {
        return WebServerConfig.builder()
            .config(config.get("server"))
            .shutdownHook(false)
            .mediaContext(mediaContext())
            .routing(builder -> {
                for (HttpFeature httpFeature : httpFeatures) {
                    builder.addFeature(httpFeature);
                }
            })
            .addFeature(CorsFeature.create(config))
            .addFeature(ContextFeature.create())
            .addFeature(OpenApiFeature.create())
            .build();
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
            .setDefaultPropertyInclusion(JsonInclude.Include.NON_EMPTY)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}

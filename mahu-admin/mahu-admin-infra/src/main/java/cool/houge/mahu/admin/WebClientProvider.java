package cool.houge.mahu.admin;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.helidon.http.media.jackson.JacksonSupport;
import io.helidon.service.registry.Service;
import io.helidon.webclient.api.WebClient;
import java.time.Duration;
import java.util.function.Supplier;

/// WebClient
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
class WebClientProvider implements Supplier<WebClient> {

    final WebClient v;

    WebClientProvider() {
        var objectMapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .findAndRegisterModules();
        this.v = WebClient.builder()
                .connectTimeout(Duration.ofSeconds(15))
                .addMediaSupport(JacksonSupport.create(objectMapper))
                .build();
    }

    @Override
    public WebClient get() {
        return v;
    }

    @Service.PreDestroy
    void destroy() {
        v.closeResource();
    }
}

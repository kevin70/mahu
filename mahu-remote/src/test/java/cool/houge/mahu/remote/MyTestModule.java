package cool.houge.mahu.remote;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.avaje.inject.spi.Builder;
import io.avaje.inject.test.TestModule;
import io.helidon.common.config.Config;
import io.helidon.http.media.jackson.JacksonSupport;
import io.helidon.webclient.api.WebClient;
import java.time.Duration;

/// 测试模块
///
/// @author ZY (kzou227@qq.com)
public class MyTestModule implements TestModule {

    @Override
    public Class<?>[] classes() {
        return new Class[] {Config.class, ObjectMapper.class, WebClient.class};
    }

    @Override
    public void build(Builder builder) {
        builder.withBean(Config.class, Config.create());
        builder.withBean(ObjectMapper.class, objectMapper());
        builder.withBean(WebClient.class, webClient());
    }

    ObjectMapper objectMapper() {
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .findAndRegisterModules();
    }

    WebClient webClient() {
        var objectMapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .findAndRegisterModules();
        return WebClient.builder()
                .connectTimeout(Duration.ofSeconds(15))
                .addMediaSupport(JacksonSupport.create(objectMapper))
                .build();
    }
}

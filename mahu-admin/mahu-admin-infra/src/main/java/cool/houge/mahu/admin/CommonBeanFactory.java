package cool.houge.mahu.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.helidon.http.media.jackson.JacksonSupport;
import io.helidon.webclient.api.WebClient;

import java.time.Duration;

/// 通用对象定义工厂
///
/// @author ZY (kzou227@qq.com)
@Factory
public class CommonBeanFactory {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .findAndRegisterModules();
    }

    @Bean
    public WebClient webClient() {
        var objectMapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .findAndRegisterModules();
        return WebClient.builder()
            .connectTimeout(Duration.ofSeconds(15))
            .addMediaSupport(JacksonSupport.create(objectMapper))
            .build();
    }
}

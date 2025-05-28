package cool.houge.mahu.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import cool.houge.mahu.config.OssConfig;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.helidon.http.media.jackson.JacksonSupport;
import io.helidon.webclient.api.WebClient;
import io.minio.MinioClient;
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

    @Bean
    public MinioClient minioClient(OssConfig ossConfig) {
        var builder = MinioClient.builder()
                .endpoint(ossConfig.endpoint())
                .credentials(ossConfig.accessKey(), ossConfig.secretKey());
        if (ossConfig.region() != null) {
            builder.region(ossConfig.region());
        }
        return builder.build();
    }
}

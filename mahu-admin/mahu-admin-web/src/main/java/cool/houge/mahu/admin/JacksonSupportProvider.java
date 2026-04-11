package cool.houge.mahu.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.helidon.common.Weighted;
import io.helidon.http.media.MediaSupport;
import io.helidon.http.media.jackson.JacksonSupport;
import io.helidon.service.registry.Service.Singleton;
import java.util.function.Supplier;

/// 自定义 Jackson
///
/// @author ZY (kzou227@qq.com)
@Singleton
class JacksonSupportProvider implements Supplier<MediaSupport>, Weighted {

    @Override
    public MediaSupport get() {
        return JacksonSupport.create(objectMapper(), "houge-jackson");
    }

    @Override
    public double weight() {
        return 1;
    }

    private ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module())
                .registerModule(new ParameterNamesModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT);
    }
}

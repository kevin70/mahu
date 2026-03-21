package cool.houge.mahu.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.helidon.service.registry.Service.Singleton;
import java.util.function.Supplier;

/// 自定义 Jackson
///
/// @author ZY (kzou227@qq.com)
@Singleton
class ObjectMapperProvider implements Supplier<ObjectMapper> {

    @Override
    public ObjectMapper get() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module())
                .registerModule(new ParameterNamesModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}

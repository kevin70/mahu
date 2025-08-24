package cool.houge.mahu.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.helidon.service.registry.Service.Singleton;
import java.util.function.Supplier;

/// Jackson ObjectMapper
///
/// @author ZY (kzou227@qq.com)
@Singleton
class ObjectMapperProvider implements Supplier<ObjectMapper> {

    @Override
    public ObjectMapper get() {
        return new ObjectMapper()
                .findAndRegisterModules()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}

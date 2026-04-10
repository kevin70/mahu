package cool.houge.mahu.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import cool.houge.mahu.util.MahuObjectMapperFactory;
import io.helidon.service.registry.Service.Singleton;
import java.util.function.Supplier;

/// Jackson ObjectMapper
///
/// @author ZY (kzou227@qq.com)
@Singleton
class ObjectMapperProvider implements Supplier<ObjectMapper> {

    @Override
    public ObjectMapper get() {
        return MahuObjectMapperFactory.create(JsonInclude.Include.NON_NULL);
    }
}

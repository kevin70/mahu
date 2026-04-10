package cool.houge.mahu.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import cool.houge.mahu.util.MahuObjectMapperFactory;
import io.helidon.service.registry.Service.Singleton;
import java.util.function.Supplier;

/// 自定义 Jackson
///
/// @author ZY (kzou227@qq.com)
@Singleton
class ObjectMapperProvider implements Supplier<ObjectMapper> {

    @Override
    public ObjectMapper get() {
        return MahuObjectMapperFactory.createDefault();
    }
}

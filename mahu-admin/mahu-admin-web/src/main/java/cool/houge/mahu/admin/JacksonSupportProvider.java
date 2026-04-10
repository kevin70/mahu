package cool.houge.mahu.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import cool.houge.mahu.util.MahuObjectMapperFactory;
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
        return MahuObjectMapperFactory.create(JsonInclude.Include.NON_DEFAULT);
    }
}

package cool.houge.mahu.admin.internal;

import io.helidon.service.registry.Service.Contract;
import io.helidon.service.registry.Service.Singleton;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/// 参考 [mapstruct](https://mapstruct.org/)
///
/// @author ZY (kzou227@qq.com)
@Contract
@AnnotateWith(Singleton.class)
@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface VoBeanMapper extends SysBeanMapper, LogBeanMapper {

    default LocalDateTime toLocalDateTime(Instant b) {
        return b != null ? b.atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }
}

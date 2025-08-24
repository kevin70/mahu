package cool.houge.mahu.admin.internal;

import cool.houge.mahu.util.PageResponse;
import io.helidon.service.registry.Service.Contract;
import io.helidon.service.registry.Service.Singleton;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Function;
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
public interface VoBeanMapper extends SystemBeanMapper, LogBeanMapper {

    default LocalDateTime toLocalDateTime(Instant b) {
        return b != null ? b.atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }

    /// 分页对象映射
    ///
    /// @param list       数据集合
    /// @param totalCount 总记录数
    /// @param fn         映射
    /// @param <T>        数据对象类型
    /// @param <R>        响应对象类型
    default <T, R> PageResponse<R> toPageResponse(List<T> list, Integer totalCount, Function<T, R> fn) {
        var resp = new PageResponse<R>();
        if (totalCount != null && totalCount > 0) {
            resp.setTotalCount(totalCount);
        }
        if (list != null) {
            resp.setItems(list.stream().map(fn).toList());
        }
        return resp;
    }
}

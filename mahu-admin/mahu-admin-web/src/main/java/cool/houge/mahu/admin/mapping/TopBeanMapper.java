package cool.houge.mahu.admin.mapping;

import com.google.common.collect.ImmutableMap;
import io.ebean.PagedList;
import io.ebean.cool.houge.NoTotalPagedList;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.jspecify.annotations.NonNull;

///
/// @author ZY (kzou227@qq.com)
public interface TopBeanMapper {

    default Long toString(UUID b) {
        return b != null ? b.getMostSignificantBits() : null;
    }

    default LocalDateTime toLocalDateTime(Instant b) {
        return b != null ? b.atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }

    default <T, R> Map<String, Object> toPageResponse(@NonNull PagedList<T> plist, @NonNull Function<T, R> mapper) {
        if (plist instanceof NoTotalPagedList<T>) {
            var items = plist.getList().stream().map(mapper).toList();
            return ImmutableMap.of("items", items);
        }

        var totalItems = plist.getTotalCount();
        if (totalItems <= 0) {
            return ImmutableMap.of("items", List.of());
        }

        var items = plist.getList().stream().map(mapper).toList();
        return ImmutableMap.of("items", items, "total_items", totalItems);
    }
}

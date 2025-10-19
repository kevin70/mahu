package cool.houge.mahu.admin.internal;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

///
/// @author ZY (kzou227@qq.com)
public interface TopBeanMapper {

    default Long toString(UUID b) {
        return b != null ? b.getMostSignificantBits() : null;
    }

    default LocalDateTime toLocalDateTime(Instant b) {
        return b != null ? b.atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }
}

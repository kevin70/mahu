package cool.houge.mahu.admin.sys.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.f4b6a3.ulid.UlidFactory;
import cool.houge.mahu.entity.sys.AdminChangeItem;
import cool.houge.mahu.entity.sys.AdminChangeLog;
import io.ebean.Database;
import io.ebean.event.changelog.ChangeLogListener;
import io.ebean.event.changelog.ChangeSet;
import io.ebean.event.changelog.TxnState;
import io.helidon.service.registry.Services;
import java.time.Duration;
import java.util.Optional;

/// 持久化变更日志
///
/// @author ZY (kzou227@qq.com)
public class PersistChangeLogListener implements ChangeLogListener {

    private static final UlidFactory ULID_FACTORY = UlidFactory.newMonotonicInstance();
    private final Cache<String, AdminChangeLog> cache =
            Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(1)).build();

    @Override
    public void log(ChangeSet changeSet) {
        if (changeSet.getTxnState() == TxnState.IN_PROGRESS) {
            var items = changeSet.getChanges().stream()
                    .map(o -> new AdminChangeItem()
                            .setId(ULID_FACTORY.create().toString())
                            .setTableName(o.getType())
                            .setTenantId(Optional.ofNullable(o.getTenantId())
                                    .map(String::valueOf)
                                    .orElse(null))
                            .setDataId(String.valueOf(o.getId()))
                            .setChangeType(o.getEvent().getCode())
                            .setEventTime(o.getEventTime())
                            .setData(o.getData())
                            .setOldData(o.getOldData()))
                    .toList();

            var changeLog = new AdminChangeLog()
                    .setId(ULID_FACTORY.create().toString())
                    .setSource(changeSet.getSource())
                    .setAdminId(Integer.parseInt(changeSet.getUserId()))
                    .setIpAddr(changeSet.getUserIpAddress())
                    .setItems(items);
            cache.put(changeSet.getTxnId(), changeLog);
        } else if (changeSet.getTxnState() == TxnState.COMMITTED) {
            var changeLog = cache.getIfPresent(changeSet.getTxnId());
            assert changeLog != null;

            var db = Services.get(Database.class);
            db.saveAll(changeLog);
            cache.invalidate(changeSet.getTxnId());
        } else {
            cache.invalidate(changeSet.getTxnId());
        }
    }
}

package cool.houge.mahu.admin.sys.repository;

import com.github.f4b6a3.ulid.UlidCreator;
import cool.houge.mahu.entity.sys.AdminChangeItem;
import cool.houge.mahu.entity.sys.AdminChangeLog;
import io.ebean.Database;
import io.ebean.event.changelog.ChangeLogListener;
import io.ebean.event.changelog.ChangeSet;
import io.ebean.event.changelog.TxnState;
import io.helidon.service.registry.Services;

/// 持久化变更日志
///
/// @author ZY (kzou227@qq.com)
public class PersistChangeLogListener implements ChangeLogListener {

    @Override
    public void log(ChangeSet changeSet) {
        if (changeSet.getTxnState() != TxnState.COMMITTED) {
            return;
        }

        var db = Services.get(Database.class);
        var items = changeSet.getChanges().stream()
                .map(o -> new AdminChangeItem()
                        .setId(UlidCreator.getMonotonicUlid().toString())
                        .setTableName(o.getType())
                        .setTenantId(String.valueOf(o.getTenantId()))
                        .setDataId(String.valueOf(o.getId()))
                        .setChangeType(o.getEvent().getCode())
                        .setEventTime(o.getEventTime())
                        .setData(o.getData())
                        .setOldData(o.getOldData()))
                .toList();
        var changeLog = new AdminChangeLog()
                .setId(UlidCreator.getMonotonicUlid().toString())
                .setSource(changeSet.getSource())
                .setIpAddr(changeSet.getUserIpAddress())
                .setAdminId(Integer.valueOf(changeSet.getUserId()))
                .setItems(items);
        db.saveAll(changeLog);
    }
}

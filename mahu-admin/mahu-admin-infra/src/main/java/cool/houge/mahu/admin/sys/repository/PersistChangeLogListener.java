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
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 持久化变更日志
///
/// @author ZY (kzou227@qq.com)
public class PersistChangeLogListener implements ChangeLogListener {

    private static final UlidFactory ULID_FACTORY = UlidFactory.newMonotonicInstance();
    private static final Logger log = LogManager.getLogger(PersistChangeLogListener.class);
    private final Cache<String, AdminChangeLog> cache =
            Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(1)).build();

    @Override
    public void log(ChangeSet changeSet) {
        var txnId = changeSet.getTxnId();
        var txnState = changeSet.getTxnState();

        if (txnState == TxnState.IN_PROGRESS) {
            this.handleInProgress(changeSet, txnId);
            return;
        }
        if (txnState == TxnState.COMMITTED) {
            this.handleCommitted(txnId);
            return;
        }

        // 其他 txn 状态（如 ROLLED_BACK）直接清理缓存，避免数据泄漏。
        cache.invalidate(txnId);
    }

    private void handleInProgress(ChangeSet changeSet, String txnId) {
        var changes = changeSet.getChanges();
        if (changes == null || changes.isEmpty()) {
            return;
        }

        // 以 txnId 为粒度聚合一条变更日志，避免同一事务多次回调时覆盖前面的 items。
        var changeLog = cache.getIfPresent(txnId);
        if (changeLog == null) {
            var userId = changeSet.getUserId();
            var adminId = parseAdminId(userId, txnId);
            if (adminId == null) {
                // 用户要求：adminId 为 null 时必须打印日志。
                log.warn("adminId 为 null，跳过审计日志 [txnId={} userId={}]", txnId, userId);
                return;
            }

            changeLog = new AdminChangeLog()
                    .setId(ULID_FACTORY.create().toString())
                    .setSource(changeSet.getSource())
                    .setAdminId(adminId)
                    .setIpAddr(changeSet.getUserIpAddress())
                    .setItems(new ArrayList<>());
        } else if (changeLog.getItems() == null) {
            changeLog.setItems(new ArrayList<>());
        }

        var items = changeLog.getItems();
        for (var o : changes) {
            items.add(new AdminChangeItem()
                    .setId(ULID_FACTORY.create().toString())
                    .setChangeLog(changeLog)
                    .setTableName(o.getType())
                    .setTenantId(o.getTenantId() == null ? null : String.valueOf(o.getTenantId()))
                    .setDataId(String.valueOf(o.getId()))
                    .setChangeType(o.getEvent().getCode())
                    .setEventTime(o.getEventTime())
                    .setData(o.getData())
                    .setOldData(o.getOldData()));
        }

        // 刷新缓存过期时间，确保 COMMITTED 能取到同一事务的审计数据。
        cache.put(txnId, changeLog);
    }

    private void handleCommitted(String txnId) {
        var changeLog = cache.getIfPresent(txnId);
        if (changeLog == null) {
            // 可能由于缓存过期、监听顺序异常或 txn 被回滚后仍触发 COMMITTED。
            log.warn("未找到待落库的审计日志，跳过 [txnId={}]", txnId);
            return;
        }

        var db = Services.get(Database.class);
        db.saveAll(changeLog);
        cache.invalidate(txnId);
    }

    private Integer parseAdminId(String userId, String txnId) {
        if (userId == null) {
            return null;
        }
        try {
            return Integer.parseInt(userId);
        } catch (RuntimeException e) {
            // adminId 解析失败时直接丢弃该事务的审计数据，避免写入脏数据。
            log.warn("解析管理员ID失败，跳过审计日志 [txnId={} userId={}]", txnId, userId, e);
            return null;
        }
    }
}

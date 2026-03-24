package cool.houge.mahu.repository.sys;

import cool.houge.mahu.entity.sys.AdminNotification;
import cool.houge.mahu.entity.sys.AdminNotificationRead;
import cool.houge.mahu.entity.sys.query.QAdminNotificationRead;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.InsertOptions;
import io.helidon.service.registry.Service;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// 管理员通知已读
@Service.Singleton
public class AdminNotificationReadRepository extends HBeanRepository<Long, AdminNotificationRead> {

    private static final InsertOptions INSERT_IGNORE_OPTIONS =
            InsertOptions.builder().onConflictNothing().build();

    public AdminNotificationReadRepository(Database db) {
        super(AdminNotificationRead.class, db);
    }

    /// 单条标记已读（幂等）
    public void markRead(int adminId, long notificationId) {
        db().insert(newRead(adminId, notificationId, Instant.now()), INSERT_IGNORE_OPTIONS);
    }

    /// 批量标记已读（幂等）
    public void markReadBatch(int adminId, List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return;
        }
        for (Long notificationId : notificationIds) {
            markRead(adminId, notificationId);
        }
    }

    /// 查询指定通知集合的已读时间
    public Map<Long, Instant> findReadAtMap(int adminId, Collection<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return Map.of();
        }
        var rows = new QAdminNotificationRead(db())
                .adminId
                .eq(adminId)
                .notification
                .id
                .in(notificationIds)
                .findList();
        var rs = new HashMap<Long, Instant>(rows.size());
        for (var row : rows) {
            rs.put(row.getNotification().getId(), row.getReadAt());
        }
        return rs;
    }

    private static AdminNotificationRead newRead(int adminId, long notificationId, Instant readAt) {
        var row = new AdminNotificationRead();
        row.setAdminId(adminId);
        row.setNotification(new AdminNotification().setId(notificationId));
        row.setReadAt(readAt);
        return row;
    }
}

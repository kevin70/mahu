package cool.houge.mahu.repository.sys;

import cool.houge.mahu.config.Status;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.AdminNotification;
import cool.houge.mahu.entity.sys.query.QAdminNotification;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/// 管理员通知
@Service.Singleton
public class AdminNotificationRepository extends HBeanRepository<Long, AdminNotification> {

    private static final String VISIBLE_TO_ADMIN_PREDICATE = "("
            + "t0.scope = ?"
            + " or exists ("
            + "select 1 from sys.admin_notification_targets ant"
            + " where ant.notification_id = t0.id and ant.admin_id = ?"
            + "))";
    private static final String READ_BY_ADMIN_PREDICATE = "exists ("
            + "select 1 from sys.admin_notification_reads anr"
            + " where anr.notification_id = t0.id and anr.admin_id = ?"
            + ")";
    private static final String UNREAD_BY_ADMIN_PREDICATE = "not exists ("
            + "select 1 from sys.admin_notification_reads anr"
            + " where anr.notification_id = t0.id and anr.admin_id = ?"
            + ")";
    private static final String POLL_CURSOR_PREDICATE =
            "(t0.updated_at > ?::timestamptz or (t0.updated_at = ?::timestamptz and t0.id > ?))";

    public AdminNotificationRepository(Database db) {
        super(AdminNotification.class, db);
    }

    public record PollCursor(Instant updatedAt, long id) {

        public PollCursor {
            Objects.requireNonNull(updatedAt, "updatedAt");
            if (id <= 0) {
                throw new IllegalArgumentException("id must be positive");
            }
        }
    }

    /// 分页查询当前管理员可见通知
    ///
    /// 可见性规则：
    /// - 状态为生效
    /// - 未过期（或不过期）
    /// - 全局通知，或命中当前管理员的定向通知
    ///
    /// read 语义：
    /// - null: 全部
    /// - true: 仅已读
    /// - false: 仅未读
    public PagedList<AdminNotification> findVisiblePage(int adminId, Page page, Boolean read) {
        var qb = visibleQuery(adminId, read);
        qb.updatedAt.desc().id.desc();
        return super.findPage(qb, page);
    }

    /// 增量轮询（基于 `updatedAt + id` 复合游标）
    ///
    /// 约定：
    /// - cursor 为空时，返回最早一批可见数据
    /// - cursor 非空时，仅返回排序位置晚于该游标的数据
    /// - includeRead=false 时，仅返回当前管理员未读
    /// - 轮询排序固定为 updatedAt ASC, id ASC
    public List<AdminNotification> pollVisible(
            int adminId, @Nullable PollCursor cursor, int limit, boolean includeRead) {
        if (limit <= 0) {
            return List.of();
        }

        var qb = visibleQuery(adminId, includeRead ? null : Boolean.FALSE);
        if (hasCursor(cursor)) {
            qb.raw(POLL_CURSOR_PREDICATE, cursor.updatedAt(), cursor.updatedAt(), cursor.id());
        }

        applyPollOrder(qb);
        return qb.setMaxRows(limit).findList();
    }

    /// 当前管理员未读数量
    public int countUnread(int adminId) {
        return visibleQuery(adminId, Boolean.FALSE).findCount();
    }

    /// 检查通知是否对当前管理员可见
    public boolean isVisibleToAdmin(long notificationId, int adminId) {
        return visibleQuery(adminId, null).id.eq(notificationId).exists();
    }

    private QAdminNotification visibleQuery(int adminId, Boolean read) {
        var now = Instant.now();
        var qb = new QAdminNotification(db());
        qb.status.eq(Status.ACTIVE.getCode());
        qb.or().expireAt.isNull().expireAt.gt(now).endOr();
        qb.raw(VISIBLE_TO_ADMIN_PREDICATE, AdminNotification.SCOPE_GLOBAL, adminId);

        if (read != null) {
            if (read) {
                qb.raw(READ_BY_ADMIN_PREDICATE, adminId);
            } else {
                qb.raw(UNREAD_BY_ADMIN_PREDICATE, adminId);
            }
        }
        return qb;
    }

    private static boolean hasCursor(@Nullable PollCursor cursor) {
        return cursor != null;
    }

    private static void applyPollOrder(QAdminNotification qb) {
        qb.updatedAt.asc().id.asc();
    }
}

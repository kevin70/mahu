package cool.houge.mahu.repository.sys;

import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.AdminNotification;
import cool.houge.mahu.entity.sys.query.QAdminNotification;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service;
import java.time.Instant;
import java.util.List;

/// 管理员通知
@Service.Singleton
public class AdminNotificationRepository extends HBeanRepository<Long, AdminNotification> {

    public AdminNotificationRepository(Database db) {
        super(AdminNotification.class, db);
    }

    /// 分页查询当前管理员可见通知
    public PagedList<AdminNotification> findVisiblePage(int adminId, Page page, Boolean read) {
        var qb = visibleQuery(adminId, read);
        qb.updatedAt.desc().id.desc();
        return super.findPage(qb, page);
    }

    /// 增量轮询（基于 id 游标）
    public List<AdminNotification> pollVisible(int adminId, Long cursor, int limit, boolean includeRead) {
        var qb = visibleQuery(adminId, includeRead ? null : Boolean.FALSE);
        if (cursor != null && cursor > 0) {
            qb.id.gt(cursor);
            qb.updatedAt.asc().id.asc();
        } else {
            qb.updatedAt.asc().id.asc();
        }
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
        qb.setDistinct(true);
        qb.status.eq(22);
        qb.or().expireAt.isNull().expireAt.gt(now).endOr();
        qb.or().scope.eq(2).targets.adminId.eq(adminId).endOr();

        if (read != null) {
            if (read) {
                qb.reads.adminId.eq(adminId);
            } else {
                qb.reads.id.isNull();
            }
        }
        return qb;
    }
}

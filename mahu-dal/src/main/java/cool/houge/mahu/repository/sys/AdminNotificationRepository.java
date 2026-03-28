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

/// 管理员通知
@Service.Singleton
public class AdminNotificationRepository extends HBeanRepository<Long, AdminNotification> {

    public AdminNotificationRepository(Database db) {
        super(AdminNotification.class, db);
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

    /// 增量轮询（基于 id 游标）
    ///
    /// 约定：
    /// - cursor 为空或<=0 时，返回最早一批可见数据
    /// - cursor>0 时，仅返回 id 大于 cursor 的新增数据
    /// - includeRead=false 时，仅返回当前管理员未读
    /// - 轮询排序固定为 updatedAt ASC, id ASC
    public List<AdminNotification> pollVisible(int adminId, Long cursor, int limit, boolean includeRead) {
        if (limit <= 0) {
            return List.of();
        }

        var qb = visibleQuery(adminId, includeRead ? null : Boolean.FALSE);
        if (hasCursor(cursor)) {
            qb.id.gt(cursor);
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
        // 查询会关联 targets / reads，使用 distinct 避免一条通知被 join 扩大为多行。
        qb.setDistinct(true);
        qb.status.eq(Status.ACTIVE.getCode());
        qb.or().expireAt.isNull().expireAt.gt(now).endOr();
        qb.or()
                .scope
                .eq(AdminNotification.SCOPE_GLOBAL)
                .targets
                .adminId
                .eq(adminId)
                .endOr();

        if (read != null) {
            if (read) {
                // 已读：必须存在当前管理员的已读记录。
                qb.reads.adminId.eq(adminId);
            } else {
                // 未读：不存在任意已读关联行（当前模型下可满足查询需求）。
                qb.reads.id.isNull();
            }
        }
        return qb;
    }

    private static boolean hasCursor(Long cursor) {
        return cursor != null && cursor > 0;
    }

    private static void applyPollOrder(QAdminNotification qb) {
        qb.updatedAt.asc().id.asc();
    }
}

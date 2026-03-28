package cool.houge.mahu.repository.sys;

import static org.assertj.core.api.Assertions.assertThat;

import cool.houge.mahu.config.Status;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.AdminNotification;
import cool.houge.mahu.testing.PostgresLiquibaseTestBase;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AdminNotificationRepositoryTest extends PostgresLiquibaseTestBase {

    /// 仓储测试统一使用负数业务 ID，避免与生产侧正向自增/常量空间混淆。
    private static final int ADMIN_ID = -101;
    private static final int OTHER_ADMIN_ID = -202;

    private AdminNotificationRepository repo() {
        return new AdminNotificationRepository(db());
    }

    private AdminNotificationReadRepository readRepo() {
        return new AdminNotificationReadRepository(db());
    }

    @Test
    void findVisiblePage_filters_by_visibility_and_read_state() {
        // 构造 6 类数据：全局未读、定向未读、全局已读、他人定向、过期、失效。
        var t1 = Instant.parse("2026-03-28T10:00:00Z");
        var t2 = Instant.parse("2026-03-28T11:00:00Z");
        var t3 = Instant.parse("2026-03-28T12:00:00Z");

        var globalUnread = saveNotification("global-unread", AdminNotification.SCOPE_GLOBAL, t1, null);
        var directedUnread = saveNotification("directed-unread", AdminNotification.SCOPE_DIRECTED, t2, null);
        var globalRead = saveNotification("global-read", AdminNotification.SCOPE_GLOBAL, t3, null);
        var directedOther = saveNotification("directed-other", AdminNotification.SCOPE_DIRECTED, t3, null);
        saveNotification("expired", AdminNotification.SCOPE_GLOBAL, t3, t1);
        saveNotification("inactive", AdminNotification.SCOPE_GLOBAL, t3, null, Status.EXPIRED.getCode());

        addTarget(directedUnread.getId(), ADMIN_ID);
        addTarget(directedOther.getId(), OTHER_ADMIN_ID);
        readRepo().markRead(ADMIN_ID, globalRead.getId());

        var page = Page.builder().page(1).pageSize(20).includeTotal(true).build();

        var allVisible = repo().findVisiblePage(ADMIN_ID, page, null);
        assertThat(allVisible.getList())
                .extracting(AdminNotification::getTitle)
                .containsExactly("global-read", "directed-unread", "global-unread");

        var readOnly = repo().findVisiblePage(ADMIN_ID, page, true);
        assertThat(readOnly.getList()).extracting(AdminNotification::getTitle).containsExactly("global-read");

        var unreadOnly = repo().findVisiblePage(ADMIN_ID, page, false);
        assertThat(unreadOnly.getList())
                .extracting(AdminNotification::getTitle)
                .containsExactly("directed-unread", "global-unread");

        assertThat(repo().countUnread(ADMIN_ID)).isEqualTo(2);
        assertThat(repo().isVisibleToAdmin(globalUnread.getId(), ADMIN_ID)).isTrue();
        assertThat(repo().isVisibleToAdmin(directedOther.getId(), ADMIN_ID)).isFalse();
        assertThat(repo().isVisibleToAdmin(-999_999L, ADMIN_ID)).isFalse();
    }

    @Test
    void pollVisible_applies_cursor_order_and_include_read() {
        // 通过固定 updatedAt，验证轮询稳定升序与 cursor 增量语义。
        var t1 = Instant.parse("2026-03-28T10:00:00Z");
        var t2 = Instant.parse("2026-03-28T11:00:00Z");
        var t3 = Instant.parse("2026-03-28T12:00:00Z");

        var first = saveNotification("poll-1", AdminNotification.SCOPE_GLOBAL, t1, null);
        var second = saveNotification("poll-2", AdminNotification.SCOPE_GLOBAL, t2, null);
        var third = saveNotification("poll-3", AdminNotification.SCOPE_GLOBAL, t3, null);
        readRepo().markRead(ADMIN_ID, second.getId());

        var firstBatch = repo().pollVisible(ADMIN_ID, null, 2, true);
        assertThat(firstBatch).extracting(AdminNotification::getTitle).containsExactly("poll-1", "poll-2");

        var cursorBatch = repo().pollVisible(ADMIN_ID, first.getId(), 10, true);
        assertThat(cursorBatch).extracting(AdminNotification::getTitle).containsExactly("poll-2", "poll-3");

        var unreadOnly = repo().pollVisible(ADMIN_ID, null, 10, false);
        assertThat(unreadOnly).extracting(AdminNotification::getTitle).containsExactly("poll-1", "poll-3");

        assertThat(repo().pollVisible(ADMIN_ID, null, 0, true)).isEmpty();
        assertThat(repo().pollVisible(ADMIN_ID, null, -1, false)).isEmpty();
    }

    private AdminNotification saveNotification(String title, int scope, Instant updatedAt, Instant expireAt) {
        return saveNotification(title, scope, updatedAt, expireAt, Status.ACTIVE.getCode());
    }

    private AdminNotification saveNotification(String title, int scope, Instant updatedAt, Instant expireAt, int status) {
        var notification = new AdminNotification()
                .setTitle(title)
                .setContent(title + "-content")
                .setScope(scope)
                .setType(1)
                .setStatus(status)
                .setPayload(Map.of())
                .setExpireAt(expireAt);

        db().save(notification);
        db().sqlUpdate("update sys.admin_notifications set updated_at = :updatedAt where id = :id")
                .setParameter("updatedAt", updatedAt)
                .setParameter("id", notification.getId())
                .execute();
        return notification;
    }

    private void addTarget(long notificationId, int adminId) {
        db().sqlUpdate(
                        "insert into sys.admin_notification_targets (notification_id, admin_id, created_at) values (:notificationId, :adminId, :createdAt)")
                .setParameter("notificationId", notificationId)
                .setParameter("adminId", adminId)
                .setParameter("createdAt", Instant.parse("2026-03-28T09:00:00Z"))
                .execute();
    }
}


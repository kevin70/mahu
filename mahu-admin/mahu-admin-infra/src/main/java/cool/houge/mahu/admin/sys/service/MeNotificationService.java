package cool.houge.mahu.admin.sys.service;

import static java.util.Base64.getUrlDecoder;
import static java.util.Base64.getUrlEncoder;

import com.google.common.primitives.Longs;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.sys.dto.MeNotificationPollResult;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.AdminNotification;
import cool.houge.mahu.repository.sys.AdminNotificationReadRepository;
import cool.houge.mahu.repository.sys.AdminNotificationRepository;
import io.ebean.PagedList;
import io.ebean.annotation.PersistBatch;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.AllArgsConstructor;

/// 我的通知服务
@Service.Singleton
@AllArgsConstructor
public class MeNotificationService {

    private final AdminNotificationRepository adminNotificationRepository;
    private final AdminNotificationReadRepository adminNotificationReadRepository;

    /// 分页查询我的通知
    @Transactional(readOnly = true)
    public PagedList<AdminNotification> page(Page page, int adminId, Boolean read) {
        return adminNotificationRepository.findVisiblePage(adminId, page, read);
    }

    /// 分页查询并附加已读状态
    @Transactional(readOnly = true)
    public PagedList<AdminNotification> pageViews(Page page, int adminId, Boolean read) {
        var plist = adminNotificationRepository.findVisiblePage(adminId, page, read);
        fillReadState(adminId, plist.getList());
        return plist;
    }

    /// 增量轮询
    @Transactional(readOnly = true)
    public MeNotificationPollResult poll(int adminId, String cursor, int limit, boolean includeRead) {
        var cursorId = decodeCursor(cursor);
        var items = adminNotificationRepository.pollVisible(adminId, cursorId, limit + 1, includeRead);
        var hasMore = items.size() > limit;
        if (hasMore) {
            items = items.subList(0, limit);
        }

        fillReadState(adminId, items);
        var nextCursor = encodeCursor(items.stream()
                .map(AdminNotification::getId)
                .max(Long::compareTo)
                .orElse(cursorId));
        var unreadCount = adminNotificationRepository.countUnread(adminId);
        return new MeNotificationPollResult(nextCursor, hasMore, unreadCount, items);
    }

    /// 单条标记已读（幂等）
    @Transactional
    public void markRead(int adminId, long notificationId) {
        if (!adminNotificationRepository.isVisibleToAdmin(notificationId, adminId)) {
            throw new BizCodeException(BizCodes.PERMISSION_DENIED, "无权限读取该通知");
        }
        adminNotificationReadRepository.markRead(adminId, notificationId);
    }

    /// 批量标记已读（幂等）
    @Transactional(batch = PersistBatch.ALL, batchSize = 200)
    public void markReadBatch(int adminId, List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return;
        }
        var uniqueIds = notificationIds.stream().distinct().toList();
        var forbiddenId = uniqueIds.stream()
                .filter(id -> !adminNotificationRepository.isVisibleToAdmin(id, adminId))
                .findFirst();
        if (forbiddenId.isPresent()) {
            throw new BizCodeException(BizCodes.PERMISSION_DENIED, "无权限读取通知: %s", forbiddenId.get());
        }
        adminNotificationReadRepository.markReadBatch(adminId, uniqueIds);
    }

    /// 将通知列表附加已读状态（回填到通知临时字段）
    void fillReadState(int adminId, List<AdminNotification> items) {
        var idList = items.stream().map(AdminNotification::getId).toList();
        var readAtMap = adminNotificationReadRepository.findReadAtMap(adminId, idList);
        for (AdminNotification item : items) {
            var readAt = readAtMap.get(item.getId());
            item.setRead(readAt != null);
            item.setReadAt(readAt);
        }
    }

    private static Long decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return 0L;
        }

        var raw = new String(getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
        var value = Longs.tryParse(raw);
        return value != null && value > 0 ? value : 0L;
    }

    private static String encodeCursor(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        return getUrlEncoder()
                .withoutPadding()
                .encodeToString(String.valueOf(id).getBytes(StandardCharsets.UTF_8));
    }
}

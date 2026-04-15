package cool.houge.mahu.admin.sys.service;

import static java.util.Base64.getUrlDecoder;
import static java.util.Base64.getUrlEncoder;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.sys.dto.MeNotificationPollResult;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.AdminNotification;
import cool.houge.mahu.repository.sys.AdminNotificationReadRepository;
import cool.houge.mahu.repository.sys.AdminNotificationRepository;
import cool.houge.mahu.repository.sys.AdminNotificationRepository.PollCursor;
import io.ebean.PagedList;
import io.ebean.annotation.PersistBatch;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.Nullable;

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
        return pollInternal(adminId, decodeCursor(cursor), limit, includeRead);
    }

    MeNotificationPollResult pollInternal(int adminId, @Nullable PollCursor pollCursor, int limit, boolean includeRead) {
        var items = adminNotificationRepository.pollVisible(adminId, pollCursor, limit + 1, includeRead);
        var hasMore = items.size() > limit;
        if (hasMore) {
            items = items.subList(0, limit);
        }

        fillReadState(adminId, items);
        var nextCursor = encodeCursor(lastCursor(items, pollCursor));
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

    static @Nullable PollCursor decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }

        try {
            var raw = new String(getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            var parts = raw.split(":", 3);
            if (parts.length != 3) {
                return null;
            }

            var epochSecond = Longs.tryParse(parts[0]);
            var nano = Ints.tryParse(parts[1]);
            var id = Longs.tryParse(parts[2]);
            if (epochSecond == null || nano == null || id == null || nano < 0 || nano > 999_999_999 || id <= 0) {
                return null;
            }
            return new PollCursor(Instant.ofEpochSecond(epochSecond, nano), id);
        } catch (IllegalArgumentException | DateTimeException _) {
            return null;
        }
    }

    static String encodeCursor(@Nullable PollCursor cursor) {
        if (cursor == null) {
            return null;
        }
        var raw = cursor.updatedAt().getEpochSecond() + ":" + cursor.updatedAt().getNano() + ":" + cursor.id();
        return getUrlEncoder()
                .withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    static @Nullable PollCursor lastCursor(
            List<AdminNotification> items, @Nullable PollCursor currentCursor) {
        if (items.isEmpty()) {
            return currentCursor;
        }

        var last = items.get(items.size() - 1);
        var lastId = last.getId();
        var lastUpdatedAt = last.getUpdatedAt();
        if (lastId == null || lastUpdatedAt == null) {
            return currentCursor;
        }
        return new PollCursor(lastUpdatedAt, lastId);
    }
}

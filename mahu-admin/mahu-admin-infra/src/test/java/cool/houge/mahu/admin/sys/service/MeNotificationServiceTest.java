package cool.houge.mahu.admin.sys.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cool.houge.mahu.admin.sys.dto.MeNotificationPollResult;
import cool.houge.mahu.entity.sys.AdminNotification;
import cool.houge.mahu.repository.sys.AdminNotificationReadRepository;
import cool.houge.mahu.repository.sys.AdminNotificationRepository;
import cool.houge.mahu.repository.sys.AdminNotificationRepository.PollCursor;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MeNotificationServiceTest {

    private static final int ADMIN_ID = 1001;

    private final AdminNotificationRepository adminNotificationRepository = mock(AdminNotificationRepository.class);
    private final AdminNotificationReadRepository adminNotificationReadRepository = mock(AdminNotificationReadRepository.class);

    @Test
    void pollInternal_uses_composite_cursor_and_encodes_last_returned_item() {
        var service = service();
        var currentCursor = new PollCursor(Instant.parse("2026-03-28T10:00:00.123456Z"), 7L);
        var first = notification(11L, Instant.parse("2026-03-28T11:00:00.123456Z"));
        var second = notification(12L, Instant.parse("2026-03-28T12:00:00.123456Z"));
        var hiddenExtra = notification(13L, Instant.parse("2026-03-28T13:00:00.123456Z"));

        when(adminNotificationRepository.pollVisible(ADMIN_ID, currentCursor, 3, true))
                .thenReturn(List.of(first, second, hiddenExtra));
        when(adminNotificationReadRepository.findReadAtMap(ADMIN_ID, List.of(11L, 12L))).thenReturn(Map.of());
        when(adminNotificationRepository.countUnread(ADMIN_ID)).thenReturn(5);

        MeNotificationPollResult result = service.pollInternal(ADMIN_ID, currentCursor, 2, true);

        assertThat(result.isHasMore()).isTrue();
        assertThat(result.getUnreadCount()).isEqualTo(5);
        assertThat(result.getItems()).extracting(AdminNotification::getId).containsExactly(11L, 12L);
        assertThat(result.getNextCursor())
                .isEqualTo(MeNotificationService.encodeCursor(new PollCursor(second.getUpdatedAt(), second.getId())));
        verify(adminNotificationRepository).pollVisible(ADMIN_ID, currentCursor, 3, true);
        verify(adminNotificationReadRepository).findReadAtMap(ADMIN_ID, List.of(11L, 12L));
    }

    @Test
    void decodeCursor_invalid_value_returns_null() {
        assertThat(MeNotificationService.decodeCursor("%%%invalid%%%")).isNull();
    }

    @Test
    void pollInternal_null_cursor_starts_from_first_page() {
        var service = service();
        when(adminNotificationRepository.pollVisible(ADMIN_ID, null, 2, false)).thenReturn(List.of());
        when(adminNotificationReadRepository.findReadAtMap(ADMIN_ID, List.of())).thenReturn(Map.of());
        when(adminNotificationRepository.countUnread(ADMIN_ID)).thenReturn(0);

        MeNotificationPollResult result = service.pollInternal(ADMIN_ID, null, 1, false);

        assertThat(result.isHasMore()).isFalse();
        assertThat(result.getNextCursor()).isNull();
        assertThat(result.getItems()).isEmpty();
        verify(adminNotificationRepository).pollVisible(ADMIN_ID, null, 2, false);
    }

    private static AdminNotification notification(Long id, Instant updatedAt) {
        return new AdminNotification().setId(id).setUpdatedAt(updatedAt);
    }

    private MeNotificationService service() {
        return new MeNotificationService(adminNotificationRepository, adminNotificationReadRepository);
    }
}

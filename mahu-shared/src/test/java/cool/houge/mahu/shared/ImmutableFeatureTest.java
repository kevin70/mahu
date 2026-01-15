package cool.houge.mahu.shared;

import static org.assertj.core.api.Assertions.assertThat;

import cool.houge.mahu.Status;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.roaringbitmap.longlong.Roaring64NavigableMap;

///
/// @author ZY (kzou227@qq.com)
class ImmutableFeatureTest {

    @Test
    void testStatus() {
        var b = ImmutableFeature.builder().status(0).build();
        assertThat(b.isActive()).isFalse();
    }

    @Test
    void testEffectiveFrom() {
        var b = ImmutableFeature.builder()
                .status(Status.ACTIVE.getCode())
                .effectiveFrom(LocalDateTime.now().plusDays(1))
                .build();
        assertThat(b.isActive()).isFalse();
    }

    @Test
    void testEffectiveTo() {
        var b = ImmutableFeature.builder()
                .status(Status.ACTIVE.getCode())
                .effectiveFrom(LocalDateTime.now().minusDays(1))
                .effectiveTo(LocalDateTime.now().minusDays(1))
                .build();
        assertThat(b.isActive()).isFalse();
    }

    @Test
    void testStartTimeAndEndTime() {
        var b = ImmutableFeature.builder()
                .status(Status.ACTIVE.getCode())
                .startTime(LocalTime.of(8, 20))
                .endTime(LocalTime.of(20, 20))
                .build();

        var date = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(7, 1));
        assertThat(b.isActive(date)).isFalse();

        var date2 = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(21, 1));
        assertThat(b.isActive(date2)).isFalse();
    }

    @Test
    void testStartTimeAndEndTimeAndCrossDay() {
        var b = ImmutableFeature.builder()
                .status(Status.ACTIVE.getCode())
                .startTime(LocalTime.of(8, 20))
                .endTime(LocalTime.of(3, 20))
                .build();

        var date = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(3, 20, 1));
        assertThat(b.isActive(date)).isFalse();

        var date2 = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(7, 1));
        assertThat(b.isActive(date2)).isFalse();
    }

    @Test
    void testWeekdays() {
        var b = ImmutableFeature.builder()
                .status(Status.ACTIVE.getCode())
                .weekdays(List.of(1, 2))
                .build();

        var date = LocalDateTime.of(2025, 11, 23, 16, 12);
        assertThat(b.isActive(date)).isFalse();
    }

    @Test
    void testDenyUserRb() {
        var denyUserRb = new Roaring64NavigableMap();
        denyUserRb.add(1);
        var b = ImmutableFeature.builder()
                .status(Status.ACTIVE.getCode())
                .denyUserRb(denyUserRb)
                .build();
        assertThat(b.isActive(1)).isFalse();
    }

    @Test
    void testAllowUserRb() {
        var allowUserRb = new Roaring64NavigableMap();
        allowUserRb.add(1);
        var b = ImmutableFeature.builder()
                .status(Status.ACTIVE.getCode())
                .allowUserRb(allowUserRb)
                .build();
        assertThat(b.isActive(2)).isFalse();
    }
}

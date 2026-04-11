package cool.houge.mahu.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DayStartTimeTest {

    @Test
    void of_string_解析日期并返回当天开始时间() {
        var startTime = DayStartTime.of("2026-04-12");

        assertEquals(LocalDateTime.of(2026, 4, 12, 0, 0), startTime.toDateTime());
    }

    @Test
    void of_local_date_相同日期对象应相等() {
        var first = DayStartTime.of(LocalDate.parse("2026-04-12"));
        var second = DayStartTime.of("2026-04-12");

        assertEquals(first, second);
    }

    @Test
    void of_string_非法日期抛出异常() {
        assertThrows(IllegalArgumentException.class, () -> DayStartTime.of("invalid-date"));
    }
}

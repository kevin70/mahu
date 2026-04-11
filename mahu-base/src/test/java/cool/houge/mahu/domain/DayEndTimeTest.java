package cool.houge.mahu.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class DayEndTimeTest {

    @Test
    void of_string_解析日期并返回当天结束时间() {
        var endTime = DayEndTime.of("2026-04-12");

        assertEquals(LocalDateTime.of(2026, 4, 12, 23, 59, 59, LocalTime.MAX.getNano()), endTime.toDateTime());
    }

    @Test
    void of_local_date_相同日期对象应相等() {
        var first = DayEndTime.of(LocalDate.parse("2026-04-12"));
        var second = DayEndTime.of("2026-04-12");

        assertEquals(first, second);
    }

    @Test
    void of_string_非法日期抛出异常() {
        assertThrows(IllegalArgumentException.class, () -> DayEndTime.of("invalid-date"));
    }
}

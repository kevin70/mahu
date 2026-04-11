package cool.houge.mahu.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class DateRangeTest {

    @Test
    void of_nullable_空字符串边界返回空区间常量() {
        var range = DateRange.ofNullable("", null);

        assertSame(DateRange.EMPTY, range);
    }

    @Test
    void of_nullable_纯空白字符串按非法日期处理() {
        assertThrows(IllegalArgumentException.class, () -> DateRange.ofNullable(" ", null));
    }

    @Test
    void from_to_按指定偏移量转换为闭区间时间点() {
        var range = DateRange.of("2026-04-12", "2026-04-13");

        assertEquals(
                Instant.parse("2026-04-12T00:00:00Z"),
                range.from(ZoneOffset.UTC).orElseThrow());
        assertEquals(
                Instant.parse("2026-04-13T23:59:59.999999999Z"),
                range.to(ZoneOffset.UTC).orElseThrow());
    }

    @Test
    void apply_from_to_命中两端边界() {
        var range = DateRange.of("2026-04-12", "2026-04-13");
        var from = new AtomicReference<Instant>();
        var to = new AtomicReference<Instant>();

        range.applyFromTo(from::set, to::set);

        assertEquals(range.from().orElseThrow(), from.get());
        assertEquals(range.to().orElseThrow(), to.get());
    }
}

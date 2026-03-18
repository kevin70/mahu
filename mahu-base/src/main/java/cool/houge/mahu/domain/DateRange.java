package cool.houge.mahu.domain;

import static java.util.Objects.requireNonNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import lombok.Value;
import org.jspecify.annotations.Nullable;

/// 日期区间
///
/// 由开始时间与结束时间组成，均为闭区间。
/// 允许一端为空，用于表示「仅起始」或「仅结束」的开区间。
///
/// @author ZY (kzou227@qq.com)
@Value
public class DateRange {

    public static final DateRange EMPTY = new DateRange(null, null);

    /// 开始时间（包含），可为空
    @Nullable
    DayStartTime start;
    /// 结束时间（包含），可为空
    @Nullable
    DayEndTime end;

    /// 使用 LocalDate 构建完整日期区间
    ///
    /// @param start 起始日期
    /// @param end 截止日期
    /// @return 日期区间
    public static DateRange of(LocalDate start, LocalDate end) {
        return new DateRange(DayStartTime.of(start), DayEndTime.of(end));
    }

    /// 使用字符串构建完整日期区间
    ///
    /// 字符串解析逻辑与 DayStartTime/DayEndTime 保持一致。
    ///
    /// @param start 起始日期字符串
    /// @param end 截止日期字符串
    /// @return 日期区间
    public static DateRange of(String start, String end) {
        return new DateRange(DayStartTime.of(start), DayEndTime.of(end));
    }

    /// 使用字符串构建可选边界的日期区间
    ///
    /// 任一参数为 null 或空字符串时，视为该端无限制。
    public static DateRange ofNullable(@Nullable String start, @Nullable String end) {
        DayStartTime s = (start == null || start.isEmpty()) ? null : DayStartTime.of(start);
        DayEndTime e = (end == null || end.isEmpty()) ? null : DayEndTime.of(end);
        return new DateRange(s, e);
    }

    public boolean isEmpty() {
        return start == null && end == null;
    }

    /// 起始时间点（可选，包含）。
    ///
    /// <p>注意：{@link DayStartTime}/{@link DayEndTime} 只描述“某天的开始/结束”这一<strong>本地时间</strong>概念，转为
    /// {@link Instant} 时必须指定一个 UTC 偏移量（{@link ZoneOffset}）。
    ///
    /// <p>该重载使用当前系统偏移量（可能随 DST 变化）。如需固定偏移量（例如永远 +08:00），请使用 {@link #from(ZoneOffset)}。
    public Optional<Instant> from() {
        return from(systemOffset());
    }

    /// 起始时间点（可选，包含）。
    ///
    /// <p>使用调用方提供的固定 UTC 偏移量将本地时间映射到时间线。
    public Optional<Instant> from(ZoneOffset offset) {
        requireNonNull(offset, "offset");
        return Optional.ofNullable(start)
                .map(DayStartTime::toDateTime)
                .map(it -> it.toInstant(offset));
    }

    /// 结束时间点（可选，包含）。
    ///
    /// <p>该重载使用当前系统偏移量（可能随 DST 变化）。如需固定偏移量，请使用 {@link #to(ZoneOffset)}。
    public Optional<Instant> to() {
        return to(systemOffset());
    }

    /// 结束时间点（可选，包含）。
    ///
    /// <p>使用调用方提供的固定 UTC 偏移量将本地时间映射到时间线。
    public Optional<Instant> to(ZoneOffset offset) {
        requireNonNull(offset, "offset");
        return Optional.ofNullable(end)
                .map(DayEndTime::toDateTime)
                .map(it -> it.toInstant(offset));
    }

    private static ZoneOffset systemOffset() {
        return OffsetDateTime.now().getOffset();
    }
}

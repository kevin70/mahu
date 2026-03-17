package cool.houge.mahu.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    /// 起始时间点（可选）
    public Optional<LocalDateTime> from() {
        return Optional.ofNullable(start).map(DayStartTime::toDateTime);
    }

    /// 结束时间点（可选）
    public Optional<LocalDateTime> to() {
        return Optional.ofNullable(end).map(DayEndTime::toDateTime);
    }
}

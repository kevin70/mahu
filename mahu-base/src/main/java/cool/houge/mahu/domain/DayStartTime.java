package cool.houge.mahu.domain;

import static java.util.Objects.requireNonNull;

import com.google.common.base.Strings;
import cool.houge.mahu.util.DateTimeUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/// 每日开始时间
///
/// 该类表示一天的开始时间，即当天的第一刻。
/// 它主要用于需要精确到天的时间计算场景。
///
/// @author ZY (kzou227@qq.com)
public class DayStartTime {

    /// 日期对象，表示该开始时间对应的日期
    private final LocalDate date;

    /// 私有构造函数，防止外部直接实例化
    ///
    /// @param date 开始时间对应的日期
    private DayStartTime(LocalDate date) {
        this.date = date;
    }

    /// 根据给定的日期字符串创建一个 DayStartTime 对象
    ///
    /// 该方法尝试解析传入的日期字符串，并将其转换为 DayStartTime 对象。
    /// 如果解析失败，则抛出 IllegalArgumentException 异常。
    ///
    /// @param date 日期字符串
    /// @return DayStartTime 对象
    public static DayStartTime of(String date) {
        requireNonNull(date, "date");
        var d = DateTimeUtils.tryParseLocalDateTime(date);
        if (d == null) {
            throw new IllegalArgumentException("非法日期: " + date);
        }
        return new DayStartTime(d.toLocalDate());
    }

    /// 根据给定的 LocalDate 创建一个 DayStartTime 对象
    ///
    /// 该方法接收一个 LocalDate 对象，并返回相应的 DayStartTime 对象。
    ///
    /// @param date LocalDate 对象
    /// @return DayStartTime 对象
    public static DayStartTime of(LocalDate date) {
        requireNonNull(date, "date");
        return new DayStartTime(date);
    }

    /// 将当前 DayStartTime 对象转换为 LocalDateTime 对象
    ///
    /// 该方法返回一个 LocalDateTime 对象，表示当天的第一刻。
    ///
    /// @return 表示当天第一刻的 LocalDateTime 对象
    public LocalDateTime toDateTime() {
        return LocalDateTime.of(date, LocalTime.MIN);
    }

    @Override
    public String toString() {
        return Strings.lenientFormat("DayStartTime(%s)", toDateTime());
    }
}

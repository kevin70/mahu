package cool.houge.mahu.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.experimental.UtilityClass;

/// 日期时间工具类，提供多种格式的时间字符串到`Instant`和`LocalDateTime`的解析功能。
///
/// 支持解析多种常见时间格式，包括纯日期、本地日期时间、UTC时间和带时区偏移的时间。
/// 所有解析方法在失败时返回`null`，避免抛出异常中断流程。
///
/// ## 支持的时间格式
/// - `yyyy-MM-dd`（仅日期，如：2024-12-01）
/// - `yyyy-MM-dd HH:mm:ss`（带空格的本地日期时间，如：2007-12-03 10:15:30）
/// - `yyyy-MM-ddTHH:mm:ss`（带T的本地日期时间，如：2007-12-03T10:15:30）
/// - `yyyy-MM-ddTHH:mm:ss.SSS`（带毫秒的本地日期时间，如：2007-12-03T10:15:30.123）
/// - `yyyy-MM-ddTHH:mm:ss.SSSZ`（UTC时间，如：2007-12-03T10:15:30.00Z）
/// - `yyyy-MM-ddTHH:mm:ssXXX`（带时区偏移的时间，如：2007-12-03T10:15:30+08:00）
///
/// @author ZY (kzou227@qq.com)
@UtilityClass
public final class DateTimeUtils {

    /// 日期格式：`yyyy-MM-dd`（符合ISO_LOCAL_DATE标准）
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    /// 本地日期时间格式（带T）：支持`yyyy-MM-ddTHH:mm:ss`和`yyyy-MM-ddTHH:mm:ss.SSS`
    private static final DateTimeFormatter LOCAL_DATE_TIME_WITH_T_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /// 本地日期时间格式（带空格）：`yyyy-MM-dd HH:mm:ss`
    private static final DateTimeFormatter LOCAL_DATE_TIME_WITH_SPACE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /// 将字符串解析为`Instant`（使用系统默认时区）。
    ///
    /// 尝试用所有支持的格式解析输入字符串，解析失败返回`null`。
    /// 对于无时区信息的本地时间，使用系统默认时区进行转换。
    ///
    /// @param text 待解析的时间字符串，可为`null`或空字符串
    /// @return 解析成功的`Instant`，失败返回`null`
    public static Instant tryParseInstant(String text) {
        return tryParseInstant(text, ZoneId.systemDefault());
    }

    /// 将字符串解析为`Instant`（使用指定时区）。
    ///
    /// 尝试用所有支持的格式解析输入字符串，解析失败返回`null`。
    /// 对于无时区信息的本地时间，使用指定时区进行转换。
    ///
    /// @param text 待解析的时间字符串，可为`null`或空字符串
    /// @param zoneId 解析本地时间时使用的时区，不能为`null`
    /// @return 解析成功的`Instant`，失败返回`null`
    /// @throws NullPointerException 如果zoneId为`null`
    public static Instant tryParseInstant(String text, ZoneId zoneId) {
        // 空值处理
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        String trimmedText = text.trim();

        // 尝试解析为日期格式 (yyyy-MM-dd)
        try {
            LocalDate date = LocalDate.parse(trimmedText, DATE_FORMATTER);
            return date.atStartOfDay(zoneId).toInstant();
        } catch (DateTimeParseException e) {
            // 解析失败，继续尝试其他格式
        }

        // 尝试解析为UTC时间 (如: 2007-12-03T10:15:30.00Z)
        try {
            return Instant.parse(trimmedText);
        } catch (DateTimeParseException e) {
            // 解析失败，继续尝试其他格式
        }

        // 尝试解析为带T的本地日期时间 (如: 2007-12-03T10:15:30 或 2007-12-03T10:15:30.123)
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(trimmedText, LOCAL_DATE_TIME_WITH_T_FORMATTER);
            return localDateTime.atZone(zoneId).toInstant();
        } catch (DateTimeParseException e) {
            // 解析失败，继续尝试其他格式
        }

        // 尝试解析为带空格的本地日期时间 (如: 2007-12-03 10:15:30)
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(trimmedText, LOCAL_DATE_TIME_WITH_SPACE_FORMATTER);
            return localDateTime.atZone(zoneId).toInstant();
        } catch (DateTimeParseException e) {
            // 所有格式解析失败
        }

        // 尝试解析为带时区偏移的时间 (如: 2007-12-03T10:15:30+08:00)
        try {
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(trimmedText);
            return offsetDateTime.toInstant();
        } catch (DateTimeParseException e) {
            // 解析失败，继续尝试其他格式
        }

        return null;
    }

    /// 将字符串解析为`LocalDateTime`（使用系统默认时区）。
    ///
    /// 尝试用所有支持的格式解析输入字符串，解析失败返回`null`。
    /// 对于带时区信息的时间，会转换为系统默认时区的本地时间。
    ///
    /// @param text 待解析的时间字符串，可为`null`或空字符串
    /// @return 解析成功的`LocalDateTime`，失败返回`null`
    public static LocalDateTime tryParseLocalDateTime(String text) {
        return tryParseLocalDateTime(text, ZoneId.systemDefault());
    }

    /// 将字符串解析为`LocalDateTime`（使用指定时区）。
    ///
    /// 尝试用所有支持的格式解析输入字符串，解析失败返回`null`。
    /// 对于带时区信息的时间，会转换为指定时区的本地时间。
    ///
    /// @param text 待解析的时间字符串，可为`null`或空字符串
    /// @param zoneId 转换带时区时间时使用的目标时区，不能为`null`
    /// @return 解析成功的`LocalDateTime`，失败返回`null`
    /// @throws NullPointerException 如果zoneId为`null`
    public static LocalDateTime tryParseLocalDateTime(String text, ZoneId zoneId) {
        // 空值处理
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        String trimmedText = text.trim();

        // 尝试解析为日期格式 (yyyy-MM-dd) -> 转换为当天的起始时间
        try {
            LocalDate date = LocalDate.parse(trimmedText, DATE_FORMATTER);
            return date.atStartOfDay();
        } catch (DateTimeParseException e) {
            // 解析失败，继续尝试其他格式
        }

        // 尝试解析为UTC时间 (如: 2007-12-03T10:15:30.00Z)
        try {
            Instant instant = Instant.parse(trimmedText);
            return LocalDateTime.ofInstant(instant, zoneId);
        } catch (DateTimeParseException e) {
            // 解析失败，继续尝试其他格式
        }

        // 尝试解析为带T的本地日期时间 (如: 2007-12-03T10:15:30 或 2007-12-03T10:15:30.123)
        try {
            return LocalDateTime.parse(trimmedText, LOCAL_DATE_TIME_WITH_T_FORMATTER);
        } catch (DateTimeParseException e) {
            // 解析失败，继续尝试其他格式
        }

        // 尝试解析为带空格的本地日期时间 (如: 2007-12-03 10:15:30)
        try {
            return LocalDateTime.parse(trimmedText, LOCAL_DATE_TIME_WITH_SPACE_FORMATTER);
        } catch (DateTimeParseException e) {
            // 所有格式解析失败
        }

        // 尝试解析为带时区偏移的时间 (如: 2007-12-03T10:15:30+08:00)
        try {
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(trimmedText);
            return offsetDateTime.atZoneSameInstant(zoneId).toLocalDateTime();
        } catch (DateTimeParseException e) {
            // 解析失败，继续尝试其他格式
        }
        return null;
    }
}

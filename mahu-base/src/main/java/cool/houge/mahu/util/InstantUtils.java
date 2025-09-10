package cool.houge.mahu.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import lombok.experimental.UtilityClass;

/// 时间工具类
///
/// @author ZY (kzou227@qq.com)
@UtilityClass
public final class InstantUtils {

    /// 将字符串转换为时间类型，如果转换失败返回 `null`。
    ///
    /// 支持转换以下的时间字符串：
    /// - `2024-12-01`
    /// - `2007-12-03T10:15:30`
    /// - `2007-12-03T10:15:30.00Z`
    ///
    /// @param text 时间字符串
    public static Instant tryParse(String text) {
        return tryParse(text, ZoneId.systemDefault());
    }

    /// 将字符串转换为时间类型，如果转换失败返回 `null`。
    ///
    /// 支持转换以下的时间字符串：
    /// - `2024-12-01`
    /// - `2007-12-03T10:15:30`
    /// - `2007-12-03T10:15:30.00Z`
    ///
    /// @param text 时间字符串
    /// @param zoneId 时区
    public static Instant tryParse(String text, ZoneId zoneId) {
        if (text == null || text.isEmpty()) return null;
        if (text.length() == 10) {
            try {
                var d = LocalDate.parse(text);
                return d.atStartOfDay(zoneId).toInstant();
            } catch (DateTimeParseException e) {
                // ignore
            }
        }

        if (text.charAt(text.length() - 1) == 'Z') {
            try {
                return Instant.parse(text);
            } catch (DateTimeParseException e) {
                // ignore
            }
        }

        try {
            return LocalDateTime.parse(text).atZone(zoneId).toInstant();
        } catch (DateTimeParseException e) {
            // ignore
        }
        return null;
    }
}

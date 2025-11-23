package cool.houge.mahu.shared;

import cool.houge.mahu.StatusCodes;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import org.roaringbitmap.longlong.ImmutableLongBitmapDataProvider;

/// 功能配置
///
/// @param id 功能 ID
/// @param module 功能模块
/// @param code 功能代码
/// @param name 功能名称
/// @param description 功能描述
/// @param status 状态
/// @param effectiveFrom 生效开始时间（精确到秒）
/// @param effectiveTo 生效结束时间（精确到秒）
/// @param startTime 每天的开始时间（精确到秒）
/// @param endTime 每天的结束时间（精确到秒，结束时间小于开始时间代表跨天）
/// @param weekdays 启用的星期
/// @param allowUserRb 可用的用户
/// @param denyUserRb 禁用的用户
/// @param extraProperties 扩展属性
/// @param extraSchema 扩展属性 JSON Schema
/// @author ZY (kzou227@qq.com)
@Builder
public record FeatureConfig(
        int id,
        String module,
        String code,
        String name,
        String description,
        int status,
        LocalDateTime effectiveFrom,
        LocalDateTime effectiveTo,
        LocalTime startTime,
        LocalTime endTime,
        List<Integer> weekdays,
        ImmutableLongBitmapDataProvider allowUserRb,
        ImmutableLongBitmapDataProvider denyUserRb,
        Map<String, Object> extraProperties,
        Map<String, Object> extraSchema) {

    /// 判断功能配置是否处于激活状态
    ///
    /// 该方法检查以下条件来确定功能配置是否当前有效：
    /// - 状态是否为激活（StatusCodes.ACTIVE）。
    /// - 当前时间是否在生效开始时间（effectiveFrom）和结束时间（effectiveTo）之间。
    /// - 如果设置了每天的开始时间和结束时间（startTime 和 endTime），则会进一步检查当前时间是否在这两个时间点之间。
    ///   注意：如果 endTime 小于等于 startTime，则表示跨天。
    /// - 如果设置了启用的星期（weekdays），则会检查当前日期是否在启用的星期列表中。
    public boolean isActive() {
        return isActive(LocalDateTime.now());
    }

    /// 判断指定用户的功能配置是否处于激活状态
    ///
    /// 该方法首先调用 `isActive()` 方法判断功能配置是否处于激活状态。如果功能配置是激活的，
    /// 则进一步检查用户 ID 是否被禁止或允许访问该功能。
    /// - 如果用户 ID 在禁止列表中，则返回 false。
    /// - 如果用户 ID 不在允许列表中且允许列表不为空，则返回 false。
    /// 否则，返回 true。
    public boolean isActive(long uid) {
        return isActive(LocalDateTime.now(), uid);
    }

    /// 判断功能配置是否在指定时间处于激活状态
    ///
    /// 该方法接受一个 `LocalDateTime` 参数 `time`，并检查以下条件来确定功能配置是否在该时间点有效：
    /// - 状态是否为激活（StatusCodes.ACTIVE）。
    /// - 指定时间是否在生效开始时间（effectiveFrom）和结束时间（effectiveTo）之间。
    /// - 如果设置了每天的开始时间和结束时间（startTime 和 endTime），则会进一步检查指定时间是否在这两个时间点之间。
    ///   注意：如果 endTime 小于等于 startTime，则表示跨天。
    /// - 如果设置了启用的星期（weekdays），则会检查指定日期是否在启用的星期列表中。
    public boolean isActive(LocalDateTime time) {
        if (status != StatusCodes.ACTIVE) {
            return false;
        }
        if (effectiveFrom != null && time.isBefore(effectiveFrom)) {
            return false;
        }
        if (effectiveTo != null && time.isAfter(effectiveTo)) {
            return false;
        }
        if (startTime != null && endTime != null) {
            var t = time.truncatedTo(ChronoUnit.SECONDS);
            var b = LocalDateTime.of(time.toLocalDate(), startTime);
            var e = endTime.isAfter(startTime)
                    ? LocalDateTime.of(time.toLocalDate(), endTime)
                    : LocalDateTime.of(time.toLocalDate(), endTime).plusDays(1);
            if (t.isBefore(b) || t.isAfter(e)) {
                return false;
            }
        }
        if (weekdays != null
                && !weekdays.isEmpty()
                && !weekdays.contains(time.getDayOfWeek().getValue())) {
            return false;
        }
        return true;
    }

    /// 判断指定用户在指定时间的功能配置是否处于激活状态
    ///
    /// 该方法接受一个 `LocalDateTime` 参数 `time` 和一个用户 ID `uid`，并检查以下条件来确定功能配置是否在该时间点对指定用户有效：
    /// - 状态是否为激活（StatusCodes.ACTIVE）。
    /// - 指定时间是否在生效开始时间（effectiveFrom）和结束时间（effectiveTo）之间。
    /// - 如果设置了每天的开始时间和结束时间（startTime 和 endTime），则会进一步检查指定时间是否在这两个时间点之间。
    ///   注意：如果 endTime 小于等于 startTime，则表示跨天。
    /// - 如果设置了启用的星期（weekdays），则会检查指定日期是否在启用的星期列表中。
    /// - 如果用户 ID 在禁止列表中，则返回 false。
    /// - 如果用户 ID 不在允许列表中且允许列表不为空，则返回 false。
    /// 否则，返回 true。
    public boolean isActive(LocalDateTime time, long uid) {
        if (isActive(time)) {
            if (denyUserRb != null && !denyUserRb.isEmpty() && denyUserRb.contains(uid)) {
                return false;
            }
            if (allowUserRb != null && !allowUserRb.isEmpty() && !allowUserRb.contains(uid)) {
                return false;
            }
        }
        return true;
    }
}

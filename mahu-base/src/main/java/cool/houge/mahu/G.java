package cool.houge.mahu;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.experimental.UtilityClass;

/// 公共的全局变量
@UtilityClass
public final class G {

    /// 项目的起始日期，用于计算时间差等操作
    public static final LocalDate EPOCH_DATE = LocalDate.of(2026, 2, 1);

    /// 项目的起始时间点，结合`EPOCH_DATE`使用，用于计算时间差等操作
    public static final LocalDateTime EPOCH_TIME = LocalDateTime.of(EPOCH_DATE, LocalTime.MIN);

    /// MDC（Mapped Diagnostic Context）日志追踪ID的键名，用于在日志中标识请求链路
    public static final String MDC_TRACE_ID = "TRACE_ID";
}

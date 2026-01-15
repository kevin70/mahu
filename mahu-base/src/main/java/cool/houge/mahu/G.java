package cool.houge.mahu;

import io.helidon.common.LazyValue;
import io.helidon.common.configurable.ScheduledThreadPoolSupplier;
import io.helidon.common.configurable.ThreadPoolSupplier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import lombok.experimental.UtilityClass;

/// 全局共享资源工具类
///
/// 该类集中管理应用程序级别的共享资源，采用工具类模式设计，
/// 确保资源的单一实例和全局可访问性。
///
/// @author ZY (kzou227@qq.com)
@UtilityClass
public final class G {

    /// 项目的起始日期，用于计算时间差等操作
    public static final LocalDate EPOCH_DATE = LocalDate.of(2026, 2, 1);

    /// 项目的起始时间点，结合`EPOCH_DATE`使用，用于计算时间差等操作
    public static final LocalDateTime EPOCH_TIME = LocalDateTime.of(EPOCH_DATE, LocalTime.MIN);

    /// MDC（Mapped Diagnostic Context）日志追踪ID的键名，用于在日志中标识请求链路
    public static final String MDC_TRACE_ID = "TRACE_ID";

    /// 全局线程池执行器。
    /// 使用虚拟线程实现，每个任务都将在单独的线程中运行。
    public static final LazyValue<ExecutorService>
        GLOBAL_EXECUTOR = LazyValue.create(() -> ThreadPoolSupplier.builder()
        .daemon(true)
        .threadNamePrefix("houge-ex")
        .virtualThreads(true)
        .build()
        .get());

    /// 全局定时任务执行器。
    /// 单一线程定时任务调度器，使用虚拟线程实现。
    public static final LazyValue<ScheduledExecutorService> SCHEDULED_EXECUTOR =
        LazyValue.create(() -> ScheduledThreadPoolSupplier.builder()
            .daemon(true)
            .threadNamePrefix("houge-sch-ex")
            .virtualThreads(true)
            .build()
            .get());
}

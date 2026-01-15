package cool.houge.mahu;

import com.github.f4b6a3.ulid.UlidCreator;
import io.helidon.common.LazyValue;
import io.helidon.common.configurable.ThreadPoolSupplier;
import io.helidon.logging.common.HelidonMdc;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
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
    public static final LazyValue<ExecutorService> HOUGE_EXEC = LazyValue.create(() -> ThreadPoolSupplier.builder()
            .daemon(true)
            .threadNamePrefix("Houge-Exec")
            .virtualThreads(true)
            .build()
            .get());

    /// 运行带有跟踪ID的 Runnable
    ///
    /// 该方法会在执行给定的 Runnable 时设置跟踪ID，并在执行完毕后移除跟踪ID。
    ///
    /// @param runnable 要执行的 Runnable
    public static void runWithTraceId(Runnable runnable) {
        try {
            HelidonMdc.set(MDC_TRACE_ID, G::traceId);
            runnable.run();
        } finally {
            HelidonMdc.remove(MDC_TRACE_ID);
        }
    }

    /// 运行带有跟踪ID的 Supplier 并返回结果
    ///
    /// 该方法会在执行给定的 Supplier 时设置跟踪ID，并在执行完毕后移除跟踪ID。
    /// 返回 Supplier 的结果。
    ///
    /// @param supplier 要执行的 Supplier
    /// @param <T> Supplier 的返回类型
    /// @return Supplier 的返回值
    public static <T> T runWithTraceId(Supplier<T> supplier) {
        try {
            HelidonMdc.set(MDC_TRACE_ID, G::traceId);
            return supplier.get();
        } finally {
            HelidonMdc.remove(MDC_TRACE_ID);
        }
    }

    private static String traceId() {
        return UlidCreator.getMonotonicUlid().toString();
    }
}

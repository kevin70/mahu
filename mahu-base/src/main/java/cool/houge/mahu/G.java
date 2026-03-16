package cool.houge.mahu;

import com.github.f4b6a3.ulid.UlidCreator;
import io.helidon.common.LazyValue;
import io.helidon.common.configurable.ThreadPoolSupplier;
import io.helidon.logging.common.HelidonMdc;
import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;

/// 全局共享资源工具类。
///
/// 集中维护跨模块使用的少量「真正全局」元素，例如：
/// - 业务起始日期 `PROJECT_START_DATE`（用于统计、按天 Redis key 的 dayIndex 等）
/// - 全局执行器 `HOUGE_EXEC`
/// - 日志 TraceId 相关常量与辅助方法
///
/// 仅放置这些基础设施级别、与具体业务模块无关的公共资源，避免成为「万能工具类」。
///
/// @author ZY (kzou227@qq.com)
@UtilityClass
public final class G {

    /// 项目业务上的起始日期。
    ///
    /// 可用于：
    /// - 统计报表中的统一时间起点（计算区间天数等）
    /// - 按天维度的 Redis Key dayIndex 计算：dayIndex = DAYS.between(PROJECT_START_DATE, 某天)
    public static final LocalDate PROJECT_START_DATE = LocalDate.of(2026, 2, 1);

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

    /// 在已有或新建的跟踪ID上下文中运行 Runnable
    ///
    /// @param runnable 要执行的 Runnable
    public static void withTraceId(Runnable runnable) {
        withTraceId(
                () -> {
                    runnable.run();
                    return null;
                });
    }

    /// 在已有或新建的跟踪ID上下文中运行 Supplier 并返回结果
    ///
    /// @param supplier 要执行的 Supplier
    /// @param <T> Supplier 的返回类型
    /// @return Supplier 的返回值
    public static <T> T withTraceId(Supplier<T> supplier) {
        var existing = HelidonMdc.get(MDC_TRACE_ID);
        if (existing.isPresent()) {
            return supplier.get();
        }
        var id = traceId();
        try {
            HelidonMdc.set(MDC_TRACE_ID, id);
            return supplier.get();
        } finally {
            HelidonMdc.remove(MDC_TRACE_ID);
        }
    }

    /// 生成日志追踪 ID
    public static String traceId() {
        return UlidCreator.getMonotonicUlid().toString();
    }
}

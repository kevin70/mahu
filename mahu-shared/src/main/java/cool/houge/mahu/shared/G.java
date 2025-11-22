package cool.houge.mahu.shared;

import cool.houge.mahu.Env;
import io.helidon.common.LazyValue;
import io.helidon.common.configurable.ScheduledThreadPoolSupplier;
import io.helidon.common.configurable.ThreadPoolSupplier;
import java.time.Duration;
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

    /// 全局线程池执行器。
    /// 使用虚拟线程实现，每个任务都将在单独的线程中运行。
    public static final LazyValue<ExecutorService> GLOBAL_EXECUTOR = LazyValue.create(() -> ThreadPoolSupplier.builder()
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

    /// 缓存1分钟过期时间。
    /// 在生产环境中为1分钟，在开发环境中为1秒。
    public static final Duration CACHE_1M_TTL = adaptCacheTtl(Duration.ofMinutes(1));

    /// 缓存5分钟过期时间。
    /// 在生产环境中为5分钟，在开发环境中为1秒。
    public static final Duration CACHE_5M_TTL = adaptCacheTtl(Duration.ofMinutes(5));

    /// 根据当前环境设置缓存时间。
    ///
    /// @param duration 默认的缓存持续时间
    /// @return 实际使用的缓存持续时间
    public static Duration adaptCacheTtl(Duration duration) {
        return Env.current().isProd() ? duration : Duration.ofSeconds(1);
    }
}

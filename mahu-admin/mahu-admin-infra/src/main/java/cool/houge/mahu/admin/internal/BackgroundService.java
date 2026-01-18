package cool.houge.mahu.admin.internal;

import io.helidon.common.configurable.ScheduledThreadPoolConfig;
import io.helidon.common.configurable.ScheduledThreadPoolSupplier;
import io.helidon.service.registry.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 背后支持服务
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@Service.RunLevel(Service.RunLevel.STARTUP + 1)
class BackgroundService {

    private static final Logger log = LogManager.getLogger(BackgroundService.class);
    private final ScheduledThreadPoolSupplier backgroundExec = ScheduledThreadPoolConfig.builder()
            .virtualThreads(true)
            .threadNamePrefix("Houge-Background-Exec")
            .corePoolSize(1)
            .build();

    @Service.PostConstruct
    void init() {
        // 1. 刷新字典

        // 2. 刷新功能特性
    }

    @Service.PreDestroy
    void destroy() {
        backgroundExec.get().shutdown();
    }
}

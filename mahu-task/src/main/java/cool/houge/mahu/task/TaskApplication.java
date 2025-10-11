package cool.houge.mahu.task;

import com.google.common.base.Stopwatch;
import io.helidon.Main;
import io.helidon.service.registry.Service;
import io.helidon.service.registry.ServiceRegistryConfig;
import io.helidon.service.registry.ServiceRegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 定时任务应用程序
///
/// @author ZY (kzou227@qq.com)
public class TaskApplication {

    private static final Logger log = LogManager.getLogger();

    public static void main(String[] args) {
        var stopWatch = Stopwatch.createStarted();
        var registryConfig = ServiceRegistryConfig.builder()
                .addRunLevel(Service.RunLevel.STARTUP)
                .addRunLevel(Service.RunLevel.SERVER)
                .allowLateBinding(false)
                .build();
        ServiceRegistryManager.create(registryConfig);
        Main.main(args);

        stopWatch.stop();
        log.info("应用启动完成，耗时 {}", stopWatch);
    }
}

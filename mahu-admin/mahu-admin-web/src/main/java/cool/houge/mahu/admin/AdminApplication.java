package cool.houge.mahu.admin;

import com.google.common.base.Stopwatch;
import io.helidon.Main;
import io.helidon.service.registry.Service.RunLevel;
import io.helidon.service.registry.ServiceRegistryConfig;
import io.helidon.service.registry.ServiceRegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 管理端 Web 应用入口。
///
/// @author ZY (kzou227@qq.com)
public class AdminApplication {

    private static final Logger log = LogManager.getLogger();

    public static void main(String[] args) {
        var startupTimer = Stopwatch.createStarted();
        log.info("Admin Web 应用启动中...");
        try {
            var registryConfig = ServiceRegistryConfig.builder()
                    .addRunLevel(RunLevel.STARTUP)
                    .addRunLevel(RunLevel.SERVER)
                    .allowLateBinding(false)
                    .build();
            ServiceRegistryManager.create(registryConfig);
            Main.main(args);

            startupTimer.stop();
            log.info("Admin Web 应用启动完成，耗时 {} ms", startupTimer.elapsed().toMillis());
        } catch (Exception e) {
            startupTimer.stop();
            log.error("Admin Web 应用启动失败，耗时 {} ms", startupTimer.elapsed().toMillis(), e);
            System.exit(1);
        }
    }
}

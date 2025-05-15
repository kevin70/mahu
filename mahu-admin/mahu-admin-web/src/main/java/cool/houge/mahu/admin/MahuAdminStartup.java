package cool.houge.mahu.admin;

import com.google.common.base.Stopwatch;
import io.avaje.inject.BeanScope;
import io.helidon.Main;
import io.helidon.config.Config;
import io.helidon.spi.HelidonStartupProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 启动后端应用程序
///
/// @author ZY (kzou227@qq.com)
public class MahuAdminStartup implements HelidonStartupProvider {

    private static final Logger log = LogManager.getLogger();
    /// IoC 容器
    BeanScope beanScope;

    @Override
    public void start(String[] args) {
        var stopWatch = Stopwatch.createStarted();
        System.setProperty("ebean.registerShutdownHook", "false");

        this.beanScope = BeanScope.builder()
                .bean(Config.class, Config.create())
                .shutdownHook(false)
                .build();
        Main.addShutdownHandler(beanScope::close);

        stopWatch.stop();
        log.info("应用启动完成，耗时 {}", stopWatch);
    }

    public static void main(String[] args) {
        Main.main(args);
    }
}

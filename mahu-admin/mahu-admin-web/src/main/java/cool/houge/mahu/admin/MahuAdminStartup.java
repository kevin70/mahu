package cool.houge.mahu.admin;

import io.avaje.inject.BeanScope;
import io.helidon.Main;
import io.helidon.config.Config;
import io.helidon.spi.HelidonStartupProvider;

/// 启动后端应用程序
///
/// @author ZY (kzou227@qq.com)
public class MahuAdminStartup implements HelidonStartupProvider {

    /// IoC 容器
    BeanScope beanScope;

    @Override
    public void start(String[] args) {
        System.setProperty("ebean.registerShutdownHook", "false");

        this.beanScope = BeanScope.builder()
                .bean(Config.class, Config.create())
                .shutdownHook(false)
                .build();
        Main.addShutdownHandler(beanScope::close);
    }

    public static void main(String[] args) {
        Main.main(args);
    }
}

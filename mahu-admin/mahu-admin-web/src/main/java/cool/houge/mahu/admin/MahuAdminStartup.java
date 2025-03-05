package cool.houge.mahu.admin;

import cool.houge.mahu.remote.MahuRemoteModule;
import io.avaje.inject.BeanScope;
import io.helidon.Main;
import io.helidon.common.config.Config;
import io.helidon.spi.HelidonStartupProvider;

import java.util.Set;
import java.util.StringJoiner;

/// 启动后端应用程序
///
/// @author ZY (kzou227@qq.com)
public class MahuAdminStartup implements HelidonStartupProvider {

    /// IoC 容器
    BeanScope beanScope;

    @Override
    public void start(String[] args) {
        System.setProperty("ebean.registerShutdownHook", "false");
        if (args.length >= 1 && "stop".equals(args[0])) {
            stop();
            System.exit(0);
            return;
        }

        beanScope = BeanScope.builder()
                .bean(Config.class, Config.create())
                .modules(new MahuAdminModule(), new MahuRemoteModule(), new MahuAdminInfraModule())
                .shutdownHook(false)
                .build();
        Main.addShutdownHandler(beanScope::close);
    }

    private static void stop() {
        var current = ProcessHandle.current();
        var currentInfo = current.info();
        ProcessHandle.allProcesses()
                .filter(p -> p.pid() != current.pid())
                .filter(process -> {
                    var info = process.info();
                    if (!info.user().equals(currentInfo.user())) {
                        return false;
                    }
                    if (!info.command().equals(currentInfo.command())) {
                        return false;
                    }

                    var args = info.arguments().map(Set::of).orElse(Set.of());
                    var currentArgs = currentInfo.arguments().map(Set::of).orElse(Set.of());
                    return currentArgs.containsAll(args);
                })
                .forEach(process -> {
                    var msg = new StringJoiner(" ");
                    msg.add("停止进程 PID:");
                    msg.add(String.valueOf(process.pid()));
                    process.info().arguments().ifPresent(args -> {
                        for (String arg : args) {
                            msg.add(arg);
                        }
                    });

                    System.out.println(msg);
                    process.destroy();
                });
    }

    public static void main(String[] args) {
        Main.main(args);
    }
}

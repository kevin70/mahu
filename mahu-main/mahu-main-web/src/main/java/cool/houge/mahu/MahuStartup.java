package cool.houge.mahu;

import cool.houge.mahu.common.PidFile;
import io.avaje.inject.BeanScope;
import io.helidon.Main;
import io.helidon.common.config.Config;
import io.helidon.common.config.GlobalConfig;
import io.helidon.spi.HelidonStartupProvider;

import java.util.Set;
import java.util.StringJoiner;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

/// 启动应用程序
///
/// @author ZY (kzou227@qq.com)
public class MahuStartup implements HelidonStartupProvider {

    /**
     * IoC 容器.
     */
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
                .bean(Config.class, GlobalConfig.config())
                .modules(new MahuWebModule(), new MahuInfraModule())
                .shutdownHook(false)
                .build();
        Main.addShutdownHandler(beanScope::close);

        // 将应用进程 PID 写入文件
        ofNullable(System.getProperty("houge.pid.file"))
                .filter(not(String::isEmpty))
                .ifPresent(PidFile::writePidFile);
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

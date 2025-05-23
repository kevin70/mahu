package cool.houge.mahu.admin;

import io.avaje.inject.spi.Builder;
import io.avaje.inject.test.TestModule;
import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import java.util.Map;

///
/// @author ZY (kzou227@qq.com)
public class MyTestModule implements TestModule {

    @Override
    public Class<?>[] classes() {
        return new Class[] {Config.class};
    }

    @Override
    public void build(Builder builder) {
        var config = Config.builder()
                .addSource(ConfigSources.create(Map.of(
                        "db.url",
                        TestBase.POSTGRE_SQL_TEST_CONTAINER.getJdbcUrl(),
                        "db.username",
                        TestBase.POSTGRE_SQL_TEST_CONTAINER.getUsername(),
                        "db.password",
                        TestBase.POSTGRE_SQL_TEST_CONTAINER.getPassword(),
                        "rabbitmq.url",
                        TestBase.RABBITMQ_TEST_CONTAINER.getAmqpUrl())))
                .addSource(ConfigSources.environmentVariables())
                .addSource(ConfigSources.systemProperties())
                .addSource(ConfigSources.file("/Users/yein/houge/mahu.yaml").optional())
                .addSource(ConfigSources.file("C:/houge/mahu.yaml").optional())
                .addSource(ConfigSources.classpath("mahu-admin.yaml"))
                .addSource(ConfigSources.classpath("mahu-admin-test.yaml"))
                .build();
        builder.withBean(Config.class, config);
    }
}

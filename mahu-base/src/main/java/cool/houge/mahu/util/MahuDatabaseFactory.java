package cool.houge.mahu.util;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.ContainerConfig;
import io.ebean.config.DatabaseConfig;
import io.ebean.config.JsonConfig;
import io.ebean.datasource.DataSourceConfig;
import java.util.function.Consumer;
import javax.sql.DataSource;

/// Mahu 统一 EBean Database 工厂。
public final class MahuDatabaseFactory {

    private MahuDatabaseFactory() {}

    public static Database create(Consumer<DatabaseConfig> customizer) {
        var config = new DatabaseConfig();
        config.setContainerConfig(new ContainerConfig());
        config.setJsonInclude(JsonConfig.Include.NON_NULL);
        config.shutdownHook(false);
        customizer.accept(config);
        return DatabaseFactory.create(config);
    }

    public static DataSourceConfig dataSourceConfig(String applicationName, DataSource dataSource) {
        return new DataSourceConfig()
                .setApplicationName(applicationName)
                .dataSource(dataSource)
                .setHeartbeatSql("SELECT 1")
                .setHeartbeatFreqSecs(15);
    }
}

package cool.houge.blma.shared.sp;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.ContainerConfig;
import io.ebean.config.DatabaseConfig;
import io.ebean.config.JsonConfig;
import io.ebean.datasource.DataSourceConfig;
import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.config.Config;
import io.helidon.service.registry.Service;
import java.util.function.Supplier;

/// 数据库
///
/// @author ZY (kzou227@qq.com)
@Weight(Weighted.DEFAULT_WEIGHT + 999)
@Service.Singleton
@Service.RunLevel(Service.RunLevel.STARTUP)
class DatabaseProvider implements Supplier<Database> {

    final Database v;

    DatabaseProvider(Config config) {
        var dbc = new DatabaseConfig();
        dbc.setName("db");
        dbc.setContainerConfig(new ContainerConfig());
        dbc.setDataSourceConfig(
                new DataSourceConfig()
                        .setApplicationName("blma-remote.sit")
                        .setUrl(config.get("db.url").asString().get())
                        .setUsername(config.get("db.username").asString().get())
                        .setPassword(config.get("db.password").asString().get())
                        .setMaxConnections(1)
                        .setHeartbeatSql("select 1")
                        .setHeartbeatFreqSecs(15)
                //
                );
        dbc.setSlowQueryMillis(100);
        dbc.setDatabaseBooleanTrue("T");
        dbc.setDatabaseBooleanFalse("F");

        dbc.setJsonInclude(JsonConfig.Include.NON_NULL);
        dbc.setCurrentUserProvider(() -> 1L);
        this.v = DatabaseFactory.create(dbc);
    }

    @Override
    public Database get() {
        return this.v;
    }

    @Service.PreDestroy
    void destroy() {
        this.v.shutdown();
    }
}

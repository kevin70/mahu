package cool.houge.mahu.task;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.ContainerConfig;
import io.ebean.config.DatabaseConfig;
import io.ebean.config.JsonConfig;
import io.ebean.datasource.DataSourceConfig;
import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.service.registry.Service;
import io.helidon.service.registry.Service.RunLevel;
import io.helidon.service.registry.Service.Singleton;
import java.util.function.Supplier;
import javax.sql.DataSource;

/// 数据库
///
/// @author ZY (kzou227@qq.com)
@Singleton
@RunLevel(RunLevel.STARTUP)
@Weight(Weighted.DEFAULT_WEIGHT + 999)
class DatabaseProvider implements Supplier<Database> {

    final Database v;

    DatabaseProvider(DataSource ds) {
        var dbc = new DatabaseConfig();
        dbc.setContainerConfig(new ContainerConfig());
        dbc.setJsonInclude(JsonConfig.Include.NON_NULL);
        dbc.shutdownHook(false);
        dbc.setDataSourceConfig(new DataSourceConfig()
                .setApplicationName("mahu-task")
                .dataSource(ds)
                .setHeartbeatSql("SELECT 1")
                .setHeartbeatFreqSecs(15));
        dbc.setSlowQueryMillis(100);
        dbc.setCurrentUserProvider(() -> {
            throw new UnsupportedOperationException("不支持获取当前用户");
        });
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

package cool.houge.mahu;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.ContainerConfig;
import io.ebean.config.CurrentUserProvider;
import io.ebean.config.DatabaseConfig;
import io.ebean.config.JsonConfig;
import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.service.registry.Service;
import io.helidon.service.registry.Service.RunLevel;
import io.helidon.service.registry.Service.Singleton;
import java.util.function.Supplier;
import javax.sql.DataSource;

/// 测试环境数据库提供者
///
/// 仅在 mahu-domain 的测试环境中使用，基于 `TestDataSourceProvider` 提供的 DataSource
/// 创建 EBean Database 实例。
@Singleton
@RunLevel(RunLevel.STARTUP)
@Weight(Weighted.DEFAULT_WEIGHT + 999)
class TestDatabaseProvider implements Supplier<Database> {

    final Database v;

    TestDatabaseProvider(DataSource ds) {
        var dbc = new DatabaseConfig()
                .containerConfig(new ContainerConfig())
                .dataSource(ds)
                .slowQueryMillis(200)
                .jsonInclude(JsonConfig.Include.NON_NULL)
                .changeLogIncludeInserts(false)
                .currentUserProvider(new CurrentUserProvider() {
                    @Override
                    public Object currentUser() {
                        return -1;
                    }
                })
                .shutdownHook(false);
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

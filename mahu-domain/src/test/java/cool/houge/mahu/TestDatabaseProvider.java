package cool.houge.mahu;

import cool.houge.mahu.util.MahuDatabaseFactory;
import io.ebean.Database;
import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.config.Config;
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

    TestDatabaseProvider(Config config, DataSource ds) {
        this.v = MahuDatabaseFactory.create(dbc -> {
            dbc.setDataSource(ds);
            dbc.setSlowQueryMillis(200);
            dbc.setChangeLogIncludeInserts(false);
            dbc.setCurrentUserProvider(() -> -1);
            dbc.setEncryptKeyManager((tableName, columnName) ->
                    () -> config.get("db.encrypt.secret-key").asString().get());
        });
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

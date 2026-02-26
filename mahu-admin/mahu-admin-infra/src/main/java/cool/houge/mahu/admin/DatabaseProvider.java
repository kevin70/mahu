package cool.houge.mahu.admin;

import cool.houge.mahu.admin.sys.repository.PersistChangeLogListener;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.ContainerConfig;
import io.ebean.config.DatabaseConfig;
import io.ebean.config.JsonConfig;
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
        var dbc = new DatabaseConfig()
                .containerConfig(new ContainerConfig())
                .dataSource(ds)
                .slowQueryMillis(200)
                .jsonInclude(JsonConfig.Include.NON_NULL)
                .currentUserProvider(new ContextCurrentUserProvider())
                .changeLogIncludeInserts(false)
                .changeLogListener(new PersistChangeLogListener())
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

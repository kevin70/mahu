package cool.houge.mahu.admin;

import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.admin.sys.repository.PersistChangeLogListener;
import cool.houge.mahu.util.Metadata;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.ContainerConfig;
import io.ebean.config.DatabaseConfig;
import io.ebean.config.JsonConfig;
import io.helidon.common.Weight;
import io.helidon.config.Config;
import io.helidon.service.registry.Service;
import io.helidon.service.registry.Service.RunLevel;
import io.helidon.service.registry.Service.Singleton;
import java.util.function.Supplier;
import javax.sql.DataSource;

/// 数据库
///
/// @author ZY (kzou227@qq.com)
@Weight(998)
@Singleton
@RunLevel(RunLevel.STARTUP)
class DatabaseProvider implements Supplier<Database> {

    final Database v;

    DatabaseProvider(Config config, DataSource ds) {
        var secretKey = config.get("db.encrypt.secret-key").asString().get();
        var dbc = new DatabaseConfig()
                .containerConfig(new ContainerConfig())
                .dataSource(ds)
                .slowQueryMillis(200)
                .encryptKeyManager((tableName, columnName) -> () -> secretKey)
                .jsonInclude(JsonConfig.Include.NON_NULL)
                .currentUserProvider(new ContextCurrentUserProvider())
                .changeLogAsync(false)
                .changeLogIncludeInserts(true)
                .changeLogPrepare(changeSet -> {
                    var userContext = AuthContext.current();
                    var metadata = Metadata.current();
                    changeSet.setUserId(String.valueOf(userContext.adminId()));
                    changeSet.setUserIpAddress(metadata.clientAddr());
                    return true;
                })
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

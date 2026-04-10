package cool.houge.mahu.admin;

import cool.houge.mahu.admin.security.AuthContext;
import cool.houge.mahu.admin.sys.repository.PersistChangeLogListener;
import cool.houge.mahu.util.MahuDatabaseFactory;
import cool.houge.mahu.util.Metadata;
import io.ebean.Database;
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
        this.v = MahuDatabaseFactory.create(dbc -> {
            dbc.setDataSource(ds);
            dbc.setSlowQueryMillis(200);
            dbc.setEncryptKeyManager((tableName, columnName) -> () -> secretKey);
            dbc.setCurrentUserProvider(new ContextCurrentUserProvider());
            dbc.setChangeLogAsync(false);
            dbc.setChangeLogIncludeInserts(true);
            dbc.setChangeLogPrepare(changeSet -> {
                var userContext = AuthContext.current();
                var metadata = Metadata.current();
                changeSet.setUserId(String.valueOf(userContext.adminId()));
                changeSet.setUserIpAddress(metadata.clientAddr());
                return true;
            });
            dbc.setChangeLogListener(new PersistChangeLogListener());
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

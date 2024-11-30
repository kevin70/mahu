package cool.houge.mahu.admin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cool.houge.mahu.admin.system.repository.AuditJourBeanPersistController;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.ContainerConfig;
import io.ebean.config.DatabaseConfig;
import io.ebean.config.JsonConfig;
import io.ebean.datasource.DataSourceConfig;
import io.helidon.common.config.Config;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

/// 数据库对象定义工厂
///
/// @author ZY (kzou227@qq.com)
@Factory
public class DbBeanFactory {

    // 应用名称
    private static final String APP_NAME = "wuneng-admin";

    @Bean(destroyPriority = 9999)
    public HikariDataSource dataSource(Config config) {
        var hikariConfig = new HikariConfig();
        hikariConfig.setPoolName(APP_NAME);
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariConfig.setJdbcUrl(config.get("db.url").asString().get());
        hikariConfig.setUsername(config.get("db.username").asString().get());
        hikariConfig.setPassword(config.get("db.password").asString().get());
        hikariConfig.setMinimumIdle(config.get("db.min-idle").asInt().get());
        hikariConfig.setMaximumPoolSize(config.get("db.max-size").asInt().get());
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.setKeepaliveTime(TimeUnit.MINUTES.toMillis(1));
        return new HikariDataSource(hikariConfig);
    }

    @Bean(destroyMethod = "shutdown", destroyPriority = 9998)
    public Database database(DataSource ds) {
        var dbc = new DatabaseConfig();
        dbc.setContainerConfig(new ContainerConfig());
        dbc.setDataSourceConfig(
                new DataSourceConfig().setApplicationName(APP_NAME).dataSource(ds));
        dbc.setSlowQueryMillis(100);
        dbc.setDatabaseBooleanTrue("T");
        dbc.setDatabaseBooleanFalse("F");

        dbc.setJsonInclude(JsonConfig.Include.NON_NULL);
        dbc.setCurrentUserProvider(new ContextCurrentUserProvider());
        dbc.add(new AuditJourBeanPersistController());
        return DatabaseFactory.create(dbc);
    }
}

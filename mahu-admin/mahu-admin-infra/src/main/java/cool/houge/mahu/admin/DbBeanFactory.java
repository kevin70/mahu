package cool.houge.mahu.admin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cool.houge.mahu.AppInstance;
import cool.houge.mahu.admin.system.repository.AuditPersistController;
import cool.houge.mahu.common.DatabaseNodeIdProvider;
import cool.houge.mahu.config.InfoConfig;
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

/// 数据库对象定义工厂
///
/// @author ZY (kzou227@qq.com)
@Factory
public class DbBeanFactory {

    @Bean(destroyPriority = 9999)
    public HikariDataSource dataSource(Config config) {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.get("db.url").asString().get());
        hikariConfig.setUsername(config.get("db.username").asString().get());
        hikariConfig.setPassword(config.get("db.password").asString().get());
        hikariConfig.setMinimumIdle(config.get("db.min-idle").asInt().get());
        // 推荐值：根据应用负载和数据库性能调整，通常为 CPU 核心数 * 2 + 1
        hikariConfig.setMaximumPoolSize(config.get("db.max-size").asInt().get());
        hikariConfig.setConnectionTestQuery("SELECT 1");

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("tcpKeepAlive", "true");
        hikariConfig.addDataSourceProperty("socketTimeout", "30000");
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public AppInstance appInstance(InfoConfig config, DataSource dataSource) {
        var nodeIdProvider = new DatabaseNodeIdProvider("admin_node_seq", dataSource);
        // 此处实现代表集群应用实例最多 256 个节点，如果节点超出 256，应该修改此处的逻辑
        var nodeId = nodeIdProvider.getNodeId() % 256 + 1;
        System.setProperty("tsid.node", String.valueOf(nodeId));
        System.setProperty("tsid.node.count", "256");
        return new AppInstance(config.name(), config.version(), nodeId);
    }

    @Bean(destroyMethod = "shutdown", destroyPriority = 9998)
    public Database database(AppInstance appInstance, DataSource ds) {
        var dbc = new DatabaseConfig();
        dbc.setContainerConfig(new ContainerConfig());
        dbc.setDataSourceConfig(
                new DataSourceConfig()
                        .setApplicationName(appInstance.getQualifiedName())
                        .dataSource(ds)
                        .setHeartbeatSql("select 1")
                        .setHeartbeatFreqSecs(15)
                //
                );
        dbc.setSlowQueryMillis(100);
        dbc.setDatabaseBooleanTrue("T");
        dbc.setDatabaseBooleanFalse("F");

        dbc.setJsonInclude(JsonConfig.Include.NON_NULL);
        dbc.setCurrentUserProvider(new ContextCurrentUserProvider());
        dbc.add(new AuditPersistController());
        return DatabaseFactory.create(dbc);
    }
}

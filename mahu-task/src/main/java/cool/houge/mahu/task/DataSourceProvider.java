package cool.houge.mahu.task;

import cool.houge.mahu.config.ConfigPrefixes;
import io.ebean.datasource.DataSourcePool;
import io.helidon.common.Weight;
import io.helidon.common.Weighted;
import io.helidon.config.Config;
import io.helidon.service.registry.Service;
import io.helidon.service.registry.Service.RunLevel;
import io.helidon.service.registry.Service.Singleton;
import java.util.function.Supplier;
import javax.sql.DataSource;

/// 数据源
///
/// @author ZY (kzou227@qq.com)
@Singleton
@RunLevel(RunLevel.STARTUP)
@Weight(Weighted.DEFAULT_WEIGHT + 1000)
class DataSourceProvider implements Supplier<DataSource> {

    final DataSourcePool v;

    DataSourceProvider(Config root) {
        var config = root.get(ConfigPrefixes.DB);
        this.v = DataSourcePool.builder()
                .name("mahu-task")
                .url(config.get("url").asString().get())
                .username(config.get("username").asString().get())
                .password(config.get("password").asString().get())
                .minConnections(config.get("min-idle").asInt().get())
                // 推荐值：根据应用负载和数据库性能调整（通常为 CPU 核心数 * 2 + 1）
                .maxConnections(config.get("max-size").asInt().get())
                .cstmtCacheSize(250)
                .pstmtCacheSize(2048)
                .heartbeatSql("SELECT 1")
                .autoCommit(true)
                .validateOnHeartbeat(true)
                .shutdownOnJvmExit(false)
                .build();
    }

    @Override
    public DataSource get() {
        return v;
    }

    @Service.PreDestroy
    void destroy() {
        v.shutdown();
    }
}

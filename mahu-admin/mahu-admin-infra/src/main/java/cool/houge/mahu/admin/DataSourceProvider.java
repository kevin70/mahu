package cool.houge.mahu.admin;

import cool.houge.mahu.config.ConfigPrefixes;
import cool.houge.mahu.util.MahuDataSourceFactory;
import io.ebean.datasource.DataSourcePool;
import io.helidon.common.Weight;
import io.helidon.config.Config;
import io.helidon.service.registry.Service;
import io.helidon.service.registry.Service.RunLevel;
import io.helidon.service.registry.Service.Singleton;
import java.util.function.Supplier;
import javax.sql.DataSource;

/// 数据源
///
/// @author ZY (kzou227@qq.com)
@Weight(999)
@Singleton
@RunLevel(RunLevel.STARTUP)
class DataSourceProvider implements Supplier<DataSource> {

    final DataSourcePool v;

    DataSourceProvider(Config root) {
        var config = root.get(ConfigPrefixes.DB);
        this.v = MahuDataSourceFactory.builder(
                        "mahu-admin",
                        config.get("url").asString().get(),
                        config.get("username").asString().get(),
                        config.get("password").asString().get())
                .minConnections(config.get("min-idle").asInt().get())
                // 推荐值：根据应用负载和数据库性能调整（通常为 CPU 核心数 * 2 + 1）
                .maxConnections(config.get("max-size").asInt().get())
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

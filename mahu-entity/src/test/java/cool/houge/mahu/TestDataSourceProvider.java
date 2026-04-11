package cool.houge.mahu;

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

/// 测试环境数据源提供者
///
/// 仅在 mahu-entity 的测试环境中使用，通过 Helidon Config 读取 `ConfigPrefixes.DB`
/// 下的配置创建数据源。
@Singleton
@RunLevel(RunLevel.STARTUP)
@Weight(Weighted.DEFAULT_WEIGHT + 1000)
class TestDataSourceProvider implements Supplier<DataSource> {

    final DataSourcePool v;

    TestDataSourceProvider(Config root) {
        var config = root.get(ConfigPrefixes.DB);
        this.v = DataSourcePool.builder()
                .name("mahu-entity-test")
                .url(config.get("url").asString().get())
                .username(config.get("username").asString().get())
                .password(config.get("password").asString().get())
                // 测试中仍保留一定 PreparedStatement 缓存，减少重复 prepare 噪音。
                .pstmtCacheSize(128)
                // 使用最轻量的探活 SQL。
                .heartbeatSql("SELECT 1")
                // 测试环境仍校验连接有效性，避免把坏连接问题误判成业务失败。
                .validateOnHeartbeat(true)
                // 关闭 JVM 自动关闭钩子，避免和测试生命周期管理重复。
                .shutdownOnJvmExit(false)
                // 测试不采集调用栈，保持连接池行为简单可预测。
                .captureStackTrace(false)
                // 每 60 秒执行一次连接池裁剪。
                .trimPoolFreqSecs(60)
                // 测试环境最长等待 1 秒拿连接，避免无意义卡住。
                .waitTimeoutMillis(1000)
                // 空闲连接 120 秒后允许回收，兼顾稳定与轻量。
                .maxInactiveTimeSecs(120)
                // 单连接最长存活 15 分钟，测试运行期内足够稳定。
                .maxAgeMinutes(15)
                .minConnections(config.get("min-idle").asInt().orElse(1))
                // 推荐值：根据应用负载和数据库性能调整（通常为 CPU 核心数 * 2 + 1）
                .maxConnections(config.get("max-size")
                        .asInt()
                        .orElse(Runtime.getRuntime().availableProcessors() * 2 + 1))
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

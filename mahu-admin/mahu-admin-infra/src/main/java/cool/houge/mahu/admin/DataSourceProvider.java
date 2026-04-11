package cool.houge.mahu.admin;

import cool.houge.mahu.config.ConfigPrefixes;
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
        this.v = DataSourcePool.builder()
                .name("mahu-admin")
                .url(config.get("url").asString().get())
                .username(config.get("username").asString().get())
                .password(config.get("password").asString().get())
                // PreparedStatement 命中频率高，较大的缓存可减少重复 SQL 的 prepare 成本。
                .pstmtCacheSize(512)
                // 低成本探活 SQL，用于心跳和坏连接探测。
                .heartbeatSql("SELECT 1")
                // 在心跳阶段主动校验连接有效性，降低业务线程拿到坏连接的概率。
                .validateOnHeartbeat(true)
                // 关闭 JVM 自动关闭钩子，交给 Helidon 生命周期统一管理。
                .shutdownOnJvmExit(false)
                // 禁止为连接借出记录调用栈，避免高并发下的额外开销。
                .captureStackTrace(false)
                // 每 60 秒执行一次连接池裁剪，平衡回收及时性与调度开销。
                .trimPoolFreqSecs(60)
                // 池满后最多等待 1 秒，避免管理端请求长时间排队。
                .waitTimeoutMillis(1000)
                // 空闲连接保留 180 秒后可回收，降低低峰期数据库连接占用。
                .maxInactiveTimeSecs(180)
                // 单连接最长存活 30 分钟，定期替换过老连接。
                .maxAgeMinutes(30)
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

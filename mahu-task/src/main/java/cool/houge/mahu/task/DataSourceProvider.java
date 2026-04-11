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
                // 保留中等 PreparedStatement 缓存，提高批量任务中的 SQL 复用效率。
                .pstmtCacheSize(256)
                // 低成本探活 SQL，用于心跳和坏连接探测。
                .heartbeatSql("SELECT 1")
                // 在心跳阶段主动校验连接有效性，减少 worker 获取失效连接的概率。
                .validateOnHeartbeat(true)
                // 关闭 JVM 自动关闭钩子，交给应用自身生命周期销毁。
                .shutdownOnJvmExit(false)
                // 禁止采集借出连接的调用栈，优先保证任务吞吐。
                .captureStackTrace(false)
                // 每 60 秒执行一次池裁剪，及时回收低峰期连接。
                .trimPoolFreqSecs(60)
                // 池满后最多等待 500ms，避免 worker 长时间堆积在拿连接阶段。
                .waitTimeoutMillis(500)
                // 空闲连接 90 秒后可回收，更积极地缩回后台连接占用。
                .maxInactiveTimeSecs(90)
                // 单连接最长存活 20 分钟，降低长连接老化风险。
                .maxAgeMinutes(20)
                .minConnections(config.get("min-idle").asInt().get())
                // 推荐值：根据应用负载和数据库性能调整（通常为 CPU 核心数 * 2 + 1）
                .maxConnections(config.get("max-size").asInt().get())
                // 后台任务以短事务为主，保持自动提交以减少事务管理开销。
                .autoCommit(true)
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

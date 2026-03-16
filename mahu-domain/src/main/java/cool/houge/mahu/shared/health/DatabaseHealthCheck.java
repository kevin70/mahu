package cool.houge.mahu.shared.health;

import static java.util.Objects.requireNonNull;

import io.ebean.Database;
import io.helidon.health.HealthCheck;
import io.helidon.health.HealthCheckResponse;
import io.helidon.health.HealthCheckResponse.Status;
import io.helidon.health.HealthCheckType;
import io.helidon.service.registry.Service.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 数据库健康检查。
///
/// 通过执行一个简单的探活 SQL（`select 1`）验证数据库连接是否可用，
/// 不依赖特定数据库方言的函数（如 `version()`），适用于多种数据库实现。
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class DatabaseHealthCheck implements HealthCheck {

    private static final Logger log = LogManager.getLogger(DatabaseHealthCheck.class);
    private final Database database;

    public DatabaseHealthCheck(Database database) {
        requireNonNull(database);
        this.database = database;
    }

    @Override
    public HealthCheckType type() {
        return HealthCheckType.READINESS;
    }

    @Override
    public String name() {
        // 为了在多数据源场景下更直观地区分，各检查项名称增加统一前缀。
        return "db-" + database.name();
    }

    @Override
    public HealthCheckResponse call() {
        try {
            // 简单探活查询，不依赖具体数据库方言
            database.sqlQuery("select 1").findOne();
            return HealthCheckResponse.builder()
                    .status(Status.UP)
                    .detail("database", database.name())
                    .build();
        } catch (Exception e) {
            log.error("数据库健康检查意外失败 {}", database, e);
            return HealthCheckResponse.builder()
                    .status(Status.ERROR)
                    .detail("error", e.getClass().getName())
                    .detail("message", e.getMessage())
                    .build();
        }
    }
}

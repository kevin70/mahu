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

/// 数据库健康检查
///
/// 定期检查数据库连接是否可用。
/// 通过执行简单的 SQL 查询（`select 1`）来验证数据库连接状态。
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

    /// 健康检查类型（就绪性检查）
    @Override
    public HealthCheckType type() {
        return HealthCheckType.READINESS;
    }

    /// 健康检查名称
    @Override
    public String name() {
        return "db-" + database.name();
    }

    /// 执行数据库健康检查
    ///
    /// @return 健康检查结果，若数据库可达返回 UP，否则返回 ERROR
    @Override
    public HealthCheckResponse call() {
        try {
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

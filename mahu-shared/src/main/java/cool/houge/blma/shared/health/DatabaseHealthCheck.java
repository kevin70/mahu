package cool.houge.blma.shared.health;

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
        return database.name();
    }

    @Override
    public HealthCheckResponse call() {
        try {
            var v = database.sqlQuery("select version()")
                    .mapToScalar(String.class)
                    .findOne();
            return HealthCheckResponse.builder()
                    .status(Status.UP)
                    .detail("version", v)
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

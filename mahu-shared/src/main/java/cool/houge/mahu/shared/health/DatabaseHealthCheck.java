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
        return "db-" + database.name();
    }

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

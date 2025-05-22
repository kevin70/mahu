package cool.houge.mahu.admin;

import io.avaje.inject.test.InjectJunitExtension;
import io.helidon.common.context.Contexts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.TestcontainersExtension;

/// 测试基类
///
/// @author ZY (kzou227@qq.com)
@ExtendWith({TestcontainersExtension.class, InjectJunitExtension.class})
@Testcontainers
public abstract class TestBase {

    /// PostgreSQL 测试容器
    public static final PostgreSQLContainer<?> POSTGRE_SQL_TEST_CONTAINER =
            new PostgreSQLContainer("postgres:17.5-alpine").withDatabaseName("mahu-sit");
    /// RabbitMQ 测试容器
    @Container
    public static final RabbitMQContainer RABBITMQ_TEST_CONTAINER =
            new RabbitMQContainer("rabbitmq:4.1.0-management-alpine");

    @BeforeEach
    void beforeEach() {
        var ctx = Contexts.context();
        ctx.ifPresent(context -> context.register(TestAuthContext.DEFAULT));
    }
}

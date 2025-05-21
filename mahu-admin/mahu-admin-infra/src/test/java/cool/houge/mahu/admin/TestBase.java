package cool.houge.mahu.admin;

import io.helidon.common.context.Contexts;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/// 测试基类
///
/// @author ZY (kzou227@qq.com)
// @ExtendWith({TestcontainersExtension.class, InjectJunitExtension.class})
@Testcontainers
public abstract class TestBase {

    /// RabbitMQ镜像
    @Container
    public static final GenericContainer<?> RABBITMQ_TEST_CONTAINER = new GenericContainer<>(
                    "m.daocloud.io/docker.io/rabbitmq:4.1.0-management-alpine")
            .withExposedPorts(5672)
            .withLabel("from", TestBase.class.getCanonicalName())
            .withReuse(true);

    @BeforeEach
    void beforeEach() {
        var ctx = Contexts.context();
        ctx.ifPresent(context -> context.register(TestAuthContext.DEFAULT));
    }
}

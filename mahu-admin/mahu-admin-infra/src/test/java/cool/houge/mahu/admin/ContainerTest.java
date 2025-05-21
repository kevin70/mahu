package cool.houge.mahu.admin;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

///
/// @author ZY (kzou227@qq.com)
class ContainerTest extends TestBase {

    @Test
    void abc() {
        System.out.println("RabbitMQ PORT: " + RABBITMQ_TEST_CONTAINER.getMappedPort(5672));

        await().atMost(10, TimeUnit.MINUTES).until(() -> false);
    }
}

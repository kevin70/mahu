package cool.houge.mahu.admin.mq;

import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.avaje.inject.test.InjectTest;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

///
/// @author ZY (kzou227@qq.com)
@InjectTest
class MQConsumerTest {

    @Inject
    MQConsumer mqConsumer;

    @Test
    void addConsumer() {
        var queue = "topic.queue.roar.mahu.send_gift_v2";
        var exchange = "gift";
        var routerKey = "send_gift_v2.TEST";
        var bodyType = TypeFactory.defaultInstance().constructType(new TypeReference<Map<String, Object>>() {});

        var state = new AtomicBoolean(false);
        mqConsumer.addConsumer(queue, exchange, routerKey, bodyType, (message) -> {
            var thread = Thread.currentThread();
            System.out.println(thread.getName() + ": " + message);
            state.set(true);
        });
        await().untilTrue(state);
    }
}

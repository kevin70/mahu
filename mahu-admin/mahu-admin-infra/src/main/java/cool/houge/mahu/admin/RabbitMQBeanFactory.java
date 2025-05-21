package cool.houge.mahu.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import cool.houge.mahu.AppInstance;
import cool.houge.mahu.admin.mq.MQConsumer;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.helidon.config.Config;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.Executors;

/// RabbitMQ 对象定义工厂
///
/// @author ZY (kzou227@qq.com)
@Factory
public class RabbitMQBeanFactory {

    @Bean
    public ConnectionFactory connectionFactory(Config config, AppInstance appInstance) {
        var uri = config.get("rabbitmq.uri").asString().get();
        var connectionFactory = new ConnectionFactory();
        try {
            connectionFactory.setConnectionTimeout(5000);
            connectionFactory.setAutomaticRecoveryEnabled(true);
            connectionFactory.setNetworkRecoveryInterval(5000);
            connectionFactory.setClientProperties(Map.of("client_name", appInstance.getQualifiedName()));
            connectionFactory.setSharedExecutor(Executors.newThreadPerTaskExecutor(
                    Thread.ofVirtual().name("RabbitMQ-", 0).factory()));
            connectionFactory.setUri(uri);
            return connectionFactory;
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalStateException(e);
        }
    }

    @Bean(initMethod = "start", destroyPriority = 9900)
    public MQConsumer mqConsumer(ConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        return new MQConsumer(connectionFactory, objectMapper);
    }
}

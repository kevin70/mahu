package cool.houge.mahu.admin.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

///
/// @author ZY (kzou227@qq.com)
public class MQConsumer implements Closeable {

    private static final Logger log = LogManager.getLogger(MQConsumer.class);

    private final ConcurrentLinkedQueue<Connection> connections = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ConnectionFactory connectionFactory;
    private final ObjectMapper objectMapper;

    public MQConsumer(ConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        this.connectionFactory = connectionFactory;
        this.objectMapper = objectMapper;
    }

    public <T> void addConsumer(
            String queue, String exchange, String routingKey, JavaType bodyType, Consumer<T> consumer) {
        if (!running.get()) {
            throw new IllegalStateException("MQConsumer 已经停止，无法继续增加消费者");
        }

        Channel channel = queueBind(queue, exchange, routingKey);
        var callback = new DefaultConsumer(channel) {
            @Override
            public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                log.info("收到RabbitMQ停止信号 {}", consumerTag, sig);
            }

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) {
                if (!running.get()) {
                    log.info("MQConsumer已经关闭，暂停消费消息 {}", () -> new String(body, StandardCharsets.UTF_8));
                    return;
                }

                try {
                    T obj = objectMapper.readValue(body, bodyType);
                    consumer.accept(obj);
                    getChannel().basicAck(envelope.getDeliveryTag(), false);
                } catch (JsonProcessingException e) {
                    log.error(
                            "RabbitMQ消息解析错误 queue={} exchange={} routerKey={} bodyType={} " + "body={}",
                            queue,
                            exchange,
                            routingKey,
                            new String(body, StandardCharsets.UTF_8),
                            bodyType,
                            e);
                } catch (Exception e) {
                    log.error(
                            "RabbitMQ消息处理错误 queue={} exchange={} routerKey={} body={}",
                            queue,
                            exchange,
                            routingKey,
                            new String(body, StandardCharsets.UTF_8),
                            e);
                }
            }
        };

        try {
            channel.basicConsume(queue, false, callback);
            log.info("开始消费MQ消息 queue={} exchange={} routingKey={}", queue, exchange, routingKey);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() {
        if (running.compareAndSet(true, false)) {
            for (Connection connection : connections) {
                try {
                    connection.close();
                } catch (IOException e) {
                    log.warn("关闭RabbitMQ [{}]错误", connection, e);
                }
            }
        }
    }

    private Channel queueBind(String queue, String exchange, String routingKey) {
        try {
            var conn = connectionFactory.newConnection();
            connections.add(conn);

            var channel = conn.createChannel();
            conn.addShutdownListener(cause -> {
                try {
                    log.info("收到RabbitMQ停止信号", cause);
                    channel.close();
                } catch (IOException | TimeoutException e) {
                    // ignore
                }
            });

            channel.queueBind(queue, exchange, routingKey);
            return channel;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (TimeoutException e) {
            throw new UncheckedTimeoutException(e);
        }
    }
}

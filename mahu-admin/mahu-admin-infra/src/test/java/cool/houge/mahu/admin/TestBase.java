package cool.houge.mahu.admin;

import io.avaje.inject.test.InjectJunitExtension;
import io.avaje.inject.test.InjectTest;
import io.ebean.Database;
import io.ebean.Transaction;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.TestcontainersExtension;

/// 测试基类
///
/// @author ZY (kzou227@qq.com)
@ExtendWith({TestcontainersExtension.class, SetupDatabaseExtension.class, InjectJunitExtension.class})
@InjectTest
@Testcontainers
public abstract class TestBase {

    /// PostgreSQL 测试容器
    @Container
    public static final PostgreSQLContainer<?> POSTGRE_SQL_TEST_CONTAINER =
            new PostgreSQLContainer<>("postgres:17.5-alpine").withReuse(true).withDatabaseName("mahu-sit");

    /// RabbitMQ 测试容器
    @Container
    public static final RabbitMQContainer RABBITMQ_TEST_CONTAINER =
            new RabbitMQContainer("rabbitmq:4.1.0-management-alpine").withReuse(true);

    /// MinIO 测试容器
    @Container
    public static final MinIOContainer MIN_IO_TEST_CONTAINER =
            new MinIOContainer("minio/minio:RELEASE.2025-05-24T17-08-30Z").withReuse(true);

    /// 数据库连接
    @Inject
    protected Database db;

    /// 测试事务
    private Transaction transaction;
    private boolean rollbackOnly;

    @BeforeEach
    void beforeEach() {
        transaction = db.beginTransaction();
    }

    @AfterEach
    void afterEach() {
        if (rollbackOnly) {
            transaction.rollback();
        } else {
            transaction.commit();
        }
    }

    /// 禁止回滚事务
    protected void disableRollback() {
        rollbackOnly = true;
    }
}

package cool.houge.mahu.testing;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.Transaction;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceBuilder;
import javax.sql.DataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/// 基于 Testcontainers + Liquibase 的集成测试基类。
///
/// ### 目标
/// - 使用真实 PostgreSQL（容器）跑持久化相关测试，避免 H2 等替代品导致的 SQL/类型差异
/// - 用 Liquibase 按项目标准迁移到目标 schema 版本，确保测试与生产变更脚本一致
/// - 每个测试用例在独立事务中执行，并在结束时回滚，保证用例之间相互隔离、可重复运行
@Testcontainers
public abstract class PostgresLiquibaseTestBase {

    /// 共享容器：在该测试 JVM 进程内复用同一个 Postgres 实例。
    ///
    /// 配合 `@BeforeAll` 只做一次 schema 迁移，显著减少大量集成测试的启动/迁移成本。
    @Container
    private static final PostgreSQLContainer PG = new PostgreSQLContainer(DockerImageName.parse("postgres:18-alpine"))
            .withDatabaseName("mahu_test")
            .withUsername("postgres")
            .withPassword("postgres");

    private static DataSource DS;
    private static Database EBEAN_DB;
    private Transaction transaction;

    /// 迁移 schema（仅一次）：用项目 Liquibase 入口变更集初始化测试库。
    ///
    /// 说明：这里仅应用 `sit` 的 context/label，避免 dev/prod 专属脚本影响测试，同时与项目既有约定保持一致。
    @BeforeAll
    static void migrateSchema() throws Exception {
        initDs();

        try (var db = liquibase.database.DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(DS.getConnection()))) {
            var accessor =
                    new ClassLoaderResourceAccessor(Thread.currentThread().getContextClassLoader());
            var liquibase = new Liquibase("/db/changelog/changelog-root.yaml", accessor, db);
            liquibase.update(new Contexts("sit"), new LabelExpression("sit"));
        }

        var dbc = new DatabaseConfig()
                .dataSource(DS)
                .currentUserProvider(() -> -1)
                .shutdownHook(false);
        EBEAN_DB = DatabaseFactory.create(dbc);
    }

    protected static Database db() {
        return EBEAN_DB;
    }

    private static void initDs() {
        DS = DataSourceBuilder.create()
                .applicationName("mahu-domain-test")
                .url(PG.getJdbcUrl())
                .username(PG.getUsername())
                .password(PG.getPassword())
                .minConnections(1)
                .maxConnections(4)
                .build();
    }

    /// 每个用例开启独立事务；配合 `setRollbackOnly()` 确保用例结束后不污染数据库状态。
    @BeforeEach
    void beginTransaction() {
        transaction = EBEAN_DB.beginTransaction();
        transaction.setRollbackOnly();
    }

    /// 统一结束事务并释放连接资源。
    ///
    /// 说明：EBean 在提交时会尊重 rollbackOnly，因此这里调用 `commit()` 的最终效果仍是回滚。
    @AfterEach
    void commitTransaction() {
        if (transaction.isActive()) {
            transaction.commit();
        }
    }
}

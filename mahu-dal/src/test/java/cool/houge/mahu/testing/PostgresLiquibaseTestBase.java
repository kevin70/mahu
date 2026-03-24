package cool.houge.mahu.testing;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.Transaction;
import io.ebean.config.DatabaseConfig;
import javax.sql.DataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class PostgresLiquibaseTestBase {

    @Container
    private static final PostgreSQLContainer PG = new PostgreSQLContainer(DockerImageName.parse("postgres:18-alpine"))
            .withDatabaseName("mahu_test")
            .withUsername("postgres")
            .withPassword("postgres");

    private static DataSource DS;
    private static Database EBEAN_DB;
    private Transaction transaction;

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
        var ds = new PGSimpleDataSource();
        ds.setDatabaseName(PG.getDatabaseName());
        ds.setUrl(PG.getJdbcUrl());
        ds.setUser(PG.getUsername());
        ds.setPassword(PG.getPassword());
        DS = ds;
    }

    @BeforeEach
    void beginTransaction() {
        transaction = EBEAN_DB.beginTransaction();
        transaction.setRollbackOnly();
    }

    @AfterEach
    void commitTransaction() {
        if (transaction.isActive()) {
            transaction.commit();
        }
    }
}

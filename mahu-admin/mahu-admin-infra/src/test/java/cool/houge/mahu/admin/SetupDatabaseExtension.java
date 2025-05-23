package cool.houge.mahu.admin;

import cool.houge.mahu.Env;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

///
/// @author ZY (kzou227@qq.com)
public class SetupDatabaseExtension implements BeforeAllCallback {

    @SuppressWarnings({"java:S1874", "deprecation"})
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        var c = TestBase.POSTGRE_SQL_TEST_CONTAINER;
        var databaseFactory = DatabaseFactory.getInstance();
        try (var connection = databaseFactory.openConnection(
                        c.getJdbcUrl(),
                        c.getUsername(),
                        c.getPassword(),
                        c.getDriverClassName(),
                        null,
                        null,
                        null,
                        new ClassLoaderResourceAccessor());
                var liquibase = new Liquibase(
                        "db/changelog/changelog-root.yaml", new ClassLoaderResourceAccessor(), connection)) {
            var contexts = new Contexts();
            contexts.add(Env.SIT.getShotName());
            liquibase.update(contexts, new LabelExpression());
        }
    }
}

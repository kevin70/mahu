package cool.houge.mahu.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import io.ebean.datasource.DataSourcePool;
import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import java.lang.reflect.Field;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DataSourceProviderTest {

    private static final Config CONFIG = Config.just(ConfigSources.create(Map.of(
                    "db.url", "jdbc:postgresql://127.0.0.1:1/mahu",
                    "db.username", "postgres",
                    "db.password", "postgres",
                    "db.min-idle", "0",
                    "db.max-size", "48"))
            .build());

    @Test
    void provider_applies_admin_pool_tuning() throws Exception {
        var provider = new DataSourceProvider(CONFIG);
        try {
            var pool = provider.v;

            assertEquals(0, pool.status(false).minSize());
            assertEquals(48, pool.status(false).maxSize());
            assertFalse(pool.isAutoCommit());
            assertEquals(1000, intField(pool, "waitTimeoutMillis"));
            assertEquals(180_000, intField(pool, "maxInactiveMillis"));
            assertEquals(30L * 60_000, longField(pool, "maxAgeMillis"));
            assertEquals(60_000L, longField(pool, "trimPoolFreqMillis"));
            assertEquals(512, intField(pool, "pstmtCacheSize"));
            assertFalse(booleanField(pool, "captureStackTrace"));
        } finally {
            provider.destroy();
        }
    }

    private static int intField(DataSourcePool pool, String fieldName) throws Exception {
        var field = field(pool, fieldName);
        return field.getInt(pool);
    }

    private static long longField(DataSourcePool pool, String fieldName) throws Exception {
        var field = field(pool, fieldName);
        return field.getLong(pool);
    }

    private static boolean booleanField(DataSourcePool pool, String fieldName) throws Exception {
        var field = field(pool, fieldName);
        return field.getBoolean(pool);
    }

    private static Field field(DataSourcePool pool, String fieldName) throws Exception {
        var field = pool.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }
}

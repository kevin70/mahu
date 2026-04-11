package cool.houge.mahu.shared.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.ebean.Database;
import io.ebean.SqlQuery;
import io.helidon.health.HealthCheckResponse;
import io.helidon.health.HealthCheckType;
import org.junit.jupiter.api.Test;

/// {@link DatabaseHealthCheck} 单元测试。
class DatabaseHealthCheckTest {

    private final Database database = mock(Database.class);
    private final SqlQuery sqlQuery = mock(SqlQuery.class);

    @Test
    void call_returns_up_when_database_query_succeeds() {
        when(database.name()).thenReturn("main");
        when(database.sqlQuery("select 1")).thenReturn(sqlQuery);
        when(sqlQuery.findOne()).thenReturn(null);

        var check = new DatabaseHealthCheck(database);
        var response = check.call();

        assertEquals(HealthCheckType.READINESS, check.type());
        assertEquals("db-main", check.name());
        assertEquals(HealthCheckResponse.Status.UP, response.status());
        assertEquals("main", response.details().get("database"));
        verify(sqlQuery).findOne();
    }

    @Test
    void call_returns_error_when_database_query_fails() {
        var cause = new IllegalStateException("boom");
        when(database.name()).thenReturn("main");
        when(database.sqlQuery("select 1")).thenReturn(sqlQuery);
        when(sqlQuery.findOne()).thenThrow(cause);

        var response = new DatabaseHealthCheck(database).call();

        assertEquals(HealthCheckResponse.Status.ERROR, response.status());
        assertEquals(IllegalStateException.class.getName(), response.details().get("error"));
        assertEquals("boom", response.details().get("message"));
    }
}

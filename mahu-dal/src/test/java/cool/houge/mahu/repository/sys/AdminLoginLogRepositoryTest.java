package cool.houge.mahu.repository.sys;

import static org.assertj.core.api.Assertions.assertThat;

import cool.houge.mahu.domain.DateRange;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.AdminLoginLog;
import cool.houge.mahu.model.query.AdminLogQuery;
import cool.houge.mahu.testing.PostgresLiquibaseTestBase;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AdminLoginLogRepositoryTest extends PostgresLiquibaseTestBase {

    private AdminLoginLogRepository repo() {
        return new AdminLoginLogRepository(db());
    }

    @Test
    void findPage_filters_by_adminId_and_createdAtRange() {
        var alice = saveLog(1, "alice", true, Instant.parse("2026-04-13T08:00:00Z"));
        var bob = saveLog(2, "bob", false, Instant.parse("2026-04-14T10:00:00Z"));

        var page = Page.builder().page(1).pageSize(20).includeTotal(true).build();

        var byAdmin = repo().findPage(AdminLogQuery.builder().adminId(1).build(), page);
        assertThat(byAdmin.getList()).extracting(AdminLoginLog::getId).containsExactly(alice.getId());

        var byDateRange = repo().findPage(
                AdminLogQuery.builder()
                        .createdAtRange(DateRange.of("2026-04-14", "2026-04-14"))
                        .build(),
                page);
        assertThat(byDateRange.getList()).extracting(AdminLoginLog::getId).containsExactly(bob.getId());
    }

    @Test
    void findPage_filters_by_username() {
        var alice = saveLog(1, "alice", true, Instant.parse("2026-04-13T08:00:00Z"));
        saveLog(2, "bob", false, Instant.parse("2026-04-14T10:00:00Z"));

        var page = Page.builder().page(1).pageSize(20).includeTotal(true).build();

        var byUsername = repo().findPage(AdminLogQuery.builder().username("alice").build(), page);

        assertThat(byUsername.getList()).extracting(AdminLoginLog::getId).containsExactly(alice.getId());
    }

    private AdminLoginLog saveLog(int adminId, String username, boolean success, Instant createdAt) {
        var entity = new AdminLoginLog()
                .setId(UUID.randomUUID())
                .setAdminId(adminId)
                .setGrantType("PASSWORD")
                .setClientId("admin")
                .setUsername(username)
                .setSuccess(success)
                .setReasonCode(success ? null : AdminLoginLog.REASON_PASSWORD_MISMATCH)
                .setReasonDetail(success ? null : "密码不匹配")
                .setIpAddr("127.0.0.1")
                .setUserAgent("JUnit");
        db().save(entity);
        db().sqlUpdate("update sys.admin_login_logs set created_at = :createdAt where id = :id")
                .setParameter("createdAt", createdAt)
                .setParameter("id", entity.getId())
                .execute();
        return entity;
    }
}

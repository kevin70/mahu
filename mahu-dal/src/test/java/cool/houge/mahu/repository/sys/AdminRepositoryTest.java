package cool.houge.mahu.repository.sys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cool.houge.mahu.config.Status;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.Admin;
import cool.houge.mahu.model.query.AdminQuery;
import cool.houge.mahu.testing.PostgresLiquibaseTestBase;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;

class AdminRepositoryTest extends PostgresLiquibaseTestBase {

    private AdminRepository repo() {
        return new AdminRepository(db());
    }

    @Test
    void findPage_filters_by_username_and_status() {
        db().saveAll(List.of(
                admin("alice", Status.ACTIVE.getCode()),
                admin("bob", Status.DISABLED.getCode()),
                admin("charlie", Status.ACTIVE.getCode())));

        var page = Page.builder().page(1).pageSize(20).includeTotal(true).build();

        var byUsername = repo().findPage(AdminQuery.builder().username("bob").build(), page);
        assertThat(byUsername.getList()).extracting(Admin::getUsername).containsExactly("bob");

        var byStatus = repo().findPage(
                        AdminQuery.builder()
                                .statusList(List.of(Status.DISABLED.getCode()))
                                .build(),
                        page);
        assertThat(byStatus.getList()).extracting(Admin::getUsername).containsExactly("bob");
    }

    @Test
    void obtainById_missing_throws_entity_not_found() {
        assertThatThrownBy(() -> repo().obtainById(-1)).isInstanceOf(EntityNotFoundException.class);
    }

    private static Admin admin(String username, int status) {
        return new Admin()
                .setDeleted(false)
                .setUsername(username)
                .setPassword("secret")
                .setNickname(username)
                .setStatus(status);
    }
}

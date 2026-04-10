package cool.houge.mahu.repository.sys;

import static org.assertj.core.api.Assertions.assertThat;

import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.Role;
import cool.houge.mahu.testing.PostgresLiquibaseTestBase;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoleRepositoryTest extends PostgresLiquibaseTestBase {

    private RoleRepository repo() {
        return new RoleRepository(db());
    }

    @Test
    void findPage_returns_persisted_roles() {
        db().saveAll(List.of(role(-101, "超级管理员"), role(-102, "运营"), role(-103, "审核员")));

        var page = Page.builder().page(1).pageSize(20).includeTotal(true).build();
        var result = repo().findPage(page);

        assertThat(result.getList()).extracting(Role::getId).contains(-101, -102, -103);
    }

    private static Role role(int id, String name) {
        return new Role()
                .setId(id)
                .setDeleted(false)
                .setName(name)
                .setOrdering(id)
                .setPermissions(List.of());
    }
}

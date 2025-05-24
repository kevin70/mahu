package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.admin.TestBase;
import cool.houge.mahu.admin.Utils;
import cool.houge.mahu.entity.system.Role;
import jakarta.inject.Inject;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/// 角色单元测试
///
/// @author ZY (kzou227@qq.com)
class RoleRepositoryTest extends TestBase {

    @Inject
    RoleRepository roleRepository;

    Role createRole() {
        var entity = Instancio.of(Role.class)
                .ignore(Utils.GEN_IGNORE_FIELDS)
                .generate(Select.field("id"), g -> g.ints().range(-100, -1))
                .lenient()
                .create();
        roleRepository.save(entity);
        return entity;
    }

    @Test
    void update() {
        var permits = List.of("A", "B", "C");
        var entity = createRole();
        entity.setPermits(permits);
        roleRepository.update(entity);

        var dbEntity = roleRepository.findById(entity.getId());
        assertThat(dbEntity).isNotNull();
        assertThat(dbEntity.getPermits()).isEqualTo(permits);
    }
}

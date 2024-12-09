package cool.houge.mahu.repository;

import cool.houge.mahu.TestTransactionBase;
import cool.houge.mahu.entity.User;
import io.ebean.annotation.SoftDelete;
import jakarta.inject.Inject;
import jakarta.persistence.Id;
import org.instancio.Model;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.of;
import static org.instancio.Select.fields;

/// 用户
///
/// @author ZY (kzou227@qq.com)
class UserRepositoryTest extends TestTransactionBase {

    final Model<User> userModel = of(User.class)
        .ignore(fields().annotated(Id.class))
        .supply(fields().annotated(SoftDelete.class), () -> false)
        .toModel();

    @Inject
    UserRepository userRepository;

    @Test
    void findByWechatAppid$Openid() {
        var entity = of(userModel).create();
        userRepository.save(entity);

        var dbEntity = userRepository.findByWechatAppid$Openid(
            entity.getWechatAppid(), entity.getWechatOpenid());
        assertThat(dbEntity)
            .isNotNull()
            .usingRecursiveComparison()
            .ignoringFields("createTime", "updateTime")
            .isEqualTo(entity);
    }

    @Test
    void findByWechatUnionid() {
        var entity = of(userModel).create();
        userRepository.save(entity);

        var dbEntity = userRepository.findByWechatUnionid(entity.getWechatUnionid());
        assertThat(dbEntity)
            .isNotNull()
            .usingRecursiveComparison()
            .ignoringFields("createTime", "updateTime")
            .isEqualTo(entity);
    }
}

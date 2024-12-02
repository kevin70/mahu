package cool.houge.mahu.repository;

import cool.houge.mahu.TestTransactionBase;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

/// 用户
///
/// @author ZY (kzou227@qq.com)
class UserRepositoryTest extends TestTransactionBase {

    @Inject
    UserRepository userRepository;

    @Test
    void findByWechatAppid$Openid() {
//        var entity = Instancio.of(User.class)
//            .ignore(fields().annotated(Id.class))
//            .supply(fields().annotated(SoftDelete.class), () -> false)
//            .create();
//
//        userRepository.save(entity);
//
//        var wechatProfile = entity.getWechatProfile().setId(entity.getId());
//        getDatabase().save(wechatProfile);

        var dbEntity = userRepository.findByWechatAppid$Openid("UHTLEANSUQ", "QRVS");
        System.out.println(dbEntity);
        System.out.println(dbEntity.getWechatProfile().getAppid());
    }
}

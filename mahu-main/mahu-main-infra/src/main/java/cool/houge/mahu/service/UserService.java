package cool.houge.mahu.service;

import cool.houge.lang.BizCodeException;
import cool.houge.lang.BizCodes;
import cool.houge.mahu.entity.User;
import cool.houge.mahu.repository.UserRepository;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 用户
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class UserService {

    @Inject
    UserRepository userRepository;

    /// 获取用户个人信息
    /// @param uid 用户 ID
    @Transactional
    public User getProfile(long uid) {
        var user = userRepository.findById(uid);
        if (user == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, "未找到用户");
        }
        return user;
    }
}

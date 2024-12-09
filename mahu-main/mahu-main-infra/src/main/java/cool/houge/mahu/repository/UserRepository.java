package cool.houge.mahu.repository;

import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.User;
import cool.houge.mahu.entity.query.QUser;
import io.ebean.Database;
import jakarta.inject.Singleton;

/// 用户
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class UserRepository extends HBeanRepository<Long, User> {

    public UserRepository(Database db) {
        super(User.class, db);
    }

    /// 使用微信的 appid 与 openid 查询用户
    ///
    /// @param appid  微信 appid
    /// @param openid 微信 openid
    public User findByWechatAppid$Openid(String appid, String openid) {
        return new QUser(db()).wechatAppid.eq(appid).wechatOpenid.eq(openid).findOne();
    }

    /// 使用微信的 unionid 查询用户
    ///
    /// @param unionid 微信 unionid
    public User findByWechatUnionid(String unionid) {
        return new QUser(db()).wechatUnionid.eq(unionid).findOne();
    }

    /// 使用指定的用户名查询用户
    ///
    /// @param username 用户名
    public User findByUsername(String username) {
        return new QUser(db()).username.eq(username).findOne();
    }
}

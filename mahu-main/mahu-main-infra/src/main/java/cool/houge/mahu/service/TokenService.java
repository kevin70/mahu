package cool.houge.mahu.service;

import cool.houge.mahu.common.GrantType;
import cool.houge.mahu.common.security.AuthContext;
import cool.houge.mahu.common.security.TokenVerifier;
import cool.houge.mahu.entity.WechatProfile;
import cool.houge.mahu.entity.system.Client;
import cool.houge.mahu.remote.wechat.Jscode2SessionPayload;
import cool.houge.mahu.remote.wechat.WechatClient;
import cool.houge.mahu.remote.wechat.WechatEncryptPayload;
import cool.houge.mahu.system.repository.ClientRepository;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.function.Supplier;

/// 令牌
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class TokenService implements TokenVerifier {

    @Inject
    ClientRepository clientRepository;

    @Inject
    WechatClient wechatClient;

    @Override
    public AuthContext verify(String token) {
        return null;
    }

    @Transactional
    public void login(TokenPayload payload) {
        if (payload.getGrantType() == GrantType.REFRESH_TOKEN) {
            //
        }
    }

    void loginByRefreshToken(TokenPayload payload) {
        //
    }

    void loginByWechatXcx(TokenPayload payload) {
        var client = clientRepository.obtainClient(payload.getClientId());
        var sessionPayload = new Jscode2SessionPayload()
            .setAppid(client.getWechatAppid())
            .setSecret(client.getWechatAppsecret())
            .setJsCode(payload.getWechatJsCode());
        var sessionResult = wechatClient.jscode2Session(sessionPayload);

        var wechatProfile = upsertWechatProfile(client, sessionResult.getUnionid(), sessionResult.getOpenid(), () -> {
            var decryptResult = wechatClient.decrypt(new WechatEncryptPayload()
                .setAppid(client.getWechatAppid())
                .setEncryptedData(payload.getWechatEncryptData())
                .setIv(payload.getWechatIv())
                .setSessionKey(sessionResult.getSessionKey()));
            return new NicknameAvatar(decryptResult.getNickName(), decryptResult.getAvatarUrl());
        });
//        var userId = wechatProfile.getUser().getId();
//        return generateToken(userId, GrantType.WECHAT_XCX, client.getClientId(), payload.getClientIp());
    }

    WechatProfile upsertWechatProfile(
        Client client, String unionid, String openid, Supplier<NicknameAvatar> nicknameAvatar) {
        WechatProfile wechatProfile = null;
//        if (Strings.isNullOrEmpty(unionid)) {
//            wechatProfile = wechatProfileRepository.findByAppid$Openid(client.getWechatAppid(), openid);
//        } else {
//            wechatProfile = wechatProfileRepository.findByUnionid(unionid);
//            // 如果没有 openid 将 openid 保存至数据库
//            if (wechatProfile != null && Strings.isNullOrEmpty(wechatProfile.getOpenid())) {
//                wechatProfile.setAppid(client.getWechatAppid()).setOpenid(openid);
//                wechatProfileRepository.update(wechatProfile);
//            }
//        }
//
//        // 数据库中没有则保存数据
//        if (wechatProfile == null) {
//            // 保存用户
//            var info = nicknameAvatar.get();
//            var user = userSharedService.save(info.nickname, info.avatar);
//
//            wechatProfile = new WechatProfile()
//                .setAppid(client.getWechatAppid())
//                .setOpenid(openid)
//                .setUnionid(unionid)
//                .setUser(user);
//            wechatProfileRepository.save(wechatProfile);
//        }
        return wechatProfile;
    }

    record NicknameAvatar(String nickname, String avatar) {
    }
}

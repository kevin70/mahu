package cool.houge.mahu.remote.wechat;

import com.fasterxml.jackson.databind.ObjectMapper;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import io.helidon.common.media.type.MediaTypes;
import io.helidon.webclient.api.WebClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;
import java.util.Base64;

import static java.util.Objects.requireNonNull;

/// 微信 API 客户端.
///
/// @author ZY (kzou227@qq.com)
@Log4j2
@Singleton
public class WechatClient {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Inject
    WebClient webClient;

    @Inject
    ObjectMapper objectMapper;

    /// 获取微信接口访问令牌.
    /// @param payload 请求数据
    public WechatTokenResult accessToken(WechatTokenPayload payload) {
        requireNonNull(payload.getAppid());
        requireNonNull(payload.getSecret());

        log.info("微信|获取访问令牌(appid={})", payload.getAppid());
        var response = webClient
                .get("https://api.weixin.qq.com/cgi-bin/token")
                .queryParam("grant_type", "client_credential")
                .queryParam("appid", payload.getAppid())
                .queryParam("secret", payload.getSecret())
                .request(WechatTokenResult.class);

        var body = response.entity();
        if (body.getErrcode() != 0) {
            log.error(
                    "微信|获取访问令牌失败 appid={} errcode={} errmsg={}",
                    payload.getAppid(),
                    body.getErrcode(),
                    body.getErrmsg());
            throw new WechatRemoteException(body.getErrcode(), body.getErrmsg());
        }

        log.info("微信|获取访问令牌(appid={})成功", payload.getAppid());
        return body;
    }

    /// 通过code换取网页授权access_token.
    ///
    /// [微信文档](https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html)
    /// @param payload 请求参数
    public SnsTokenResult accessToken(SnsTokenPayload payload) {
        requireNonNull(payload.getAppid());
        requireNonNull(payload.getSecret());

        var appid = payload.getAppid();
        var secret = payload.getSecret();
        var code = payload.getCode();
        var response = webClient
                .get("https://api.weixin.qq.com/sns/oauth2/access_token")
                .queryParam("appid", appid)
                .queryParam("secret", secret)
                .queryParam("code", code)
                .queryParam("grant_type", "authorization_code")
                .request(SnsTokenResult.class);

        var body = response.entity();
        if (body.getErrcode() != null && body.getErrcode() != 0) {
            log.error(
                    "微信|获取访问SNS令牌失败 appid={} errcode={} errmsg={}",
                    payload.getAppid(),
                    body.getErrcode(),
                    body.getErrmsg());
            throw new WechatRemoteException(body.getErrcode(), body.getErrmsg());
        }
        return body;
    }

    /// 拉取用户信息(需scope为 snsapi_userinfo).
    ///
    /// [微信文档](https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html)
    /// @param payload 请求参数
    public SnsUserinfoResult userinfo(SnsUserinfoPayload payload) {
        var at = payload.getAccessToken();
        var openid = payload.getOpenid();
        var lang = payload.getLang();
        var response = webClient
                .get("https://api.weixin.qq.com/sns/userinfo")
                .queryParam("access_token", at)
                .queryParam("openid", openid)
                .queryParam("lang", lang)
                .request(SnsUserinfoResult.class);
        return response.entity();
    }

    /// 解密微信数据.
    /// @param payload 参数
    public WechatDecryptResult decrypt(WechatEncryptPayload payload) {
        // 初始化
        Cipher cipher;
        AlgorithmParameters parameters;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            parameters = AlgorithmParameters.getInstance("AES");
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("不支持微信数据解密", e);
        }

        try {
            var keyBytes = Base64.getDecoder().decode(payload.getSessionKey());
            var ivBytes = Base64.getDecoder().decode(payload.getIv());
            parameters.init(new IvParameterSpec(ivBytes));
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), parameters);
        } catch (InvalidParameterSpecException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new BizCodeException(BizCodes.INTERNAL, "非法的微信解密密钥参数", e);
        }

        byte[] content;
        try {
            var dataBytes = Base64.getDecoder().decode(payload.getEncryptedData());
            content = cipher.doFinal(dataBytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "非法的微信解密数据", e);
        }

        try {
            return objectMapper.readValue(content, WechatDecryptResult.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /// [登录凭证校验](https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html)
    ///
    /// @param payload 请求数据
    public Jscode2SessionResult jscode2Session(Jscode2SessionPayload payload) {
        requireNonNull(payload.getAppid());
        requireNonNull(payload.getSecret());
        requireNonNull(payload.getJsCode());

        log.info("微信|小程序登录凭证校验 appid={} jsCode={}", payload.getAppid(), payload.getJsCode());
        var response = webClient
                .get("https://api.weixin.qq.com/sns/jscode2session")
                .queryParam("grant_type", "authorization_code")
                .queryParam("appid", payload.getAppid())
                .queryParam("secret", payload.getSecret())
                .queryParam("js_code", payload.getJsCode())
                .request(Jscode2SessionResult.class);

        var body = response.entity();
        if (body.getErrcode() != null && body.getErrcode() > 0) {
            log.error(
                    "微信|获取小程序获取访问信息失败 appid={} errcode={} errmsg={}",
                    payload.getAppid(),
                    body.getErrcode(),
                    body.getErrmsg());
            throw new WechatRemoteException(body.getErrcode(), body.getErrmsg());
        }

        log.info("微信|小程序登录凭证校验成功 jsCode={}", payload.getJsCode());
        return body;
    }

    /// [获取手机号](https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-info/phone-number/getPhoneNumber.html)
    /// @param accessToken 访问令牌
    /// @param payload 请求数据
    public GetUserPhoneNumberResult getUserPhoneNumber(String accessToken, GetUserPhoneNumberPayload payload) {
        try (var response = webClient
                .post("https://api.weixin.qq.com/wxa/business/getuserphonenumber")
                .queryParam("access_token", accessToken)
                .contentType(MediaTypes.APPLICATION_JSON)

                // 不要直接提交实体对象，因微信服务接口强制要求 content-length 参数，故采用手动拼接 JSON 字符串代替。
                // 参考：https://developers.weixin.qq.com/community/develop/doc/00004cff6144c81dda9e29eb351c00?_at=1711711383319
                .submit("{\"code\":\"" + payload.getCode() + "\"}")) {
            return response.as(GetUserPhoneNumberResult.class);
        }
    }
}

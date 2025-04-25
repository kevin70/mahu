package cool.houge.mahu.remote.wechat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.config.WechatPayConfig;
import io.helidon.common.configurable.Resource;
import io.helidon.common.media.type.MediaTypes;
import io.helidon.http.HeaderNames;
import io.helidon.webclient.api.WebClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/// 微信支付
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class WechatPayClient {

    private final ConcurrentHashMap<String, PrivateKey> PRIVATE_KEYS = new ConcurrentHashMap<>();

    @Inject
    WebClient webClient;

    @Inject
    ObjectMapper objectMapper;

    /// JSAPI 下单.
    public JsapiPrepayResult prepayJsapi(WechatPayConfig config, PrepayPayload payload) {
        payload.setMchid(config.mchid()).setAppid(config.appid()).setNotifyUrl(config.notifyUrl());

        String body;
        try {
            body = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new BizCodeException(BizCodes.INTERNAL, "序列化微信下单数据错误", e);
        }

        var canonicalUrl = "/v3/pay/transactions/jsapi";
        var authorization = getAuthorization(config, "POST", canonicalUrl, body);
        try (var response = webClient
            .post(config.baseUri() + canonicalUrl)
            .contentType(MediaTypes.APPLICATION_JSON)
            .accept(MediaTypes.APPLICATION_JSON)
            .header(HeaderNames.AUTHORIZATION, authorization)
            .submit(body)) {
            var result = response.as(JsapiPrepayResult.class);
            if (result.getCode() != null) {
                throw new BizCodeException(BizCodes.INTERNAL, result.getCode() + ": " + result.getMessage());
            }

            fillData(config, result);
            return result;
        }
    }

    /// 解密微信 APIv3 支付回调数据.
    ///
    /// @param payload 微信回调数据
    public WechatPayCallbackResult decryptTransaction(WechatCallbackPayload payload) {
        try {
            return objectMapper.readValue(decryptApiv3(payload), WechatPayCallbackResult.class);
        } catch (IOException e) {
            throw new BizCodeException(BizCodes.UNKNOWN, "解析微信支付通知回调数据出现错误", e);
        }
    }

    /// 退款申请.
    ///
    /// @param config  微信配置
    /// @param payload 退款数据
    public WechatRefundResult refund(WechatPayConfig config, WechatRefundPayload payload) {
        payload.setNotifyUrl(config.notifyUrl());
        String body;
        try {
            body = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new BizCodeException(BizCodes.INTERNAL, "序列化微信下单数据错误", e);
        }

        var canonicalUrl = "/v3/refund/domestic/refunds";
        var authorization = getAuthorization(config, "POST", canonicalUrl, body);
        try (var response = webClient
            .post(config.baseUri() + canonicalUrl)
            .contentType(MediaTypes.APPLICATION_JSON)
            .accept(MediaTypes.APPLICATION_JSON)
            .header(HeaderNames.AUTHORIZATION, authorization)
            .submit(body)) {
            var result = response.as(WechatRefundResult.class);
            if (result.getCode() != null) {
                throw new BizCodeException(
                    BizCodes.INTERNAL, "微信退款失败 " + result.getCode() + ": " + result.getMessage());
            }
            return result;
        }
    }

    /// 解密微信 APIv3 退款回调数据.
    ///
    /// @param payload 微信回调数据
    public WechatRefundCallbackResult decryptRefund(WechatCallbackPayload payload) {
        try {
            return objectMapper.readValue(decryptApiv3(payload), WechatRefundCallbackResult.class);
        } catch (IOException e) {
            throw new BizCodeException(BizCodes.UNKNOWN, "解析微信支付通知回调数据出现错误", e);
        }
    }

    /// 解密微信 APIv3 支付回调数据.
    ///
    /// @param payload 微信回调数据
    byte[] decryptApiv3(WechatCallbackPayload payload) {
        try {
            var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            var key = new SecretKeySpec("Huashanlunjian166741895131769182".getBytes(StandardCharsets.UTF_8), "AES");
            var spec = new GCMParameterSpec(128, payload.getNonce().getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            cipher.updateAAD(payload.getAssociatedData().getBytes(StandardCharsets.UTF_8));

            return cipher.doFinal(Base64.getDecoder().decode(payload.getCiphertext()));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new BizCodeException(BizCodes.INTERNAL, "不支持 AES/GCM/NoPadding", e);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new BizCodeException(BizCodes.UNAVAILABLE, "非法的微信 apiv3 密钥", e);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "解密数据失败", e);
        }
    }

    void fillData(WechatPayConfig config, JsapiPrepayResult result) {
        result.setAppid(config.appid())
            .setNonceStr(Objects.toString(Math.random()))
            .setTimeStamp(Objects.toString(System.currentTimeMillis() / 1000))
            .setPackageValue("prepay_id=" + result.getPrepayId())
            .setSignType("RSA");

        var signatureStr = result.getAppid() + "\n" + result.getTimeStamp() + "\n" + result.getNonceStr() + "\n"
            + result.getPackageValue() + "\n";
        var signature = signature0(config.privateKeyResource(), signatureStr.getBytes(StandardCharsets.UTF_8));
        result.setPaySign(signature);
    }

    String getAuthorization(WechatPayConfig config, String method, String canonicalUrl, String body) {
        String nonceStr = Objects.toString(Math.random());
        long timestamp = System.currentTimeMillis() / 1000;
        var signatureStr = method + "\n" + canonicalUrl + "\n" + timestamp + "\n" + nonceStr + "\n" + body + "\n";
        var signature = signature0(config.privateKeyResource(), signatureStr.getBytes(StandardCharsets.UTF_8));
        return "WECHATPAY2-SHA256-RSA2048 mchid=\"" + config.mchid() + "\","
            + "nonce_str=\"" + nonceStr + "\","
            + "timestamp=\"" + timestamp + "\","
            + "serial_no=\"" + config.serialNo() + "\","
            + "signature=\"" + signature + "\"";
    }

    String signature0(String privateKeyResource, byte[] content) {
        try {
            var sign = Signature.getInstance("SHA256WithRSA");
            var key = PRIVATE_KEYS.computeIfAbsent(privateKeyResource, this::loadPrivateKey);
            sign.initSign(key);
            sign.update(content);
            return Base64.getEncoder().encodeToString(sign.sign());
        } catch (NoSuchAlgorithmException e) {
            throw new BizCodeException(BizCodes.UNKNOWN, "不支持 SHA256withRSA 签名算法", e);
        } catch (SignatureException e) {
            throw new BizCodeException(BizCodes.INTERNAL, "SHA256withRSA 签名异常", e);
        } catch (InvalidKeyException e) {
            throw new BizCodeException(BizCodes.INTERNAL, "非法的密钥", e);
        }
    }

    PrivateKey loadPrivateKey(String keyFile) {
        try {
            var keyStr = Resource.create(keyFile)
                .string()
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
            var bytes = Base64.getDecoder().decode(keyStr);
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));
        } catch (InvalidKeySpecException e) {
            throw new BizCodeException(BizCodes.INTERNAL, "非法的RSA私钥", e);
        } catch (NoSuchAlgorithmException e) {
            throw new BizCodeException(BizCodes.INTERNAL, "未找到RSA加密算法", e);
        }
    }
}

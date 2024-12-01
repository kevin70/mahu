package cool.houge.mahu.remote.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/// 微信解密数据
///
/// @author ZY (kzou227@qq.com)
@Data
public class WechatEncryptPayload {

    /// 小程序 appId
    private String appid;
    /// 加密的数据
    @JsonProperty("encrypted_data")
    private String encryptedData;
    /// 加密秘钥
    @JsonProperty("session_key")
    private String sessionKey;
    /// 偏移量
    private String iv;
}

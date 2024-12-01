package cool.houge.mahu.remote.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/// 微信支付回调数据.
///
/// @author ZY (kzou227@qq.com)
@Data
public class WechatCallbackPayload {

    /// 回调类型.
    ///
    /// 可选值：
    /// - `transaction` 支付
    /// - `refund` 退款
    ///
    @JsonProperty("original_type")
    private String originalType;
    /// 附加数据.
    @JsonProperty("associated_data")
    private String associatedData;
    /// 加密使用的随机串.
    private String nonce;
    /// Base64编码后的开启/停用结果数据密文.
    private String ciphertext;
}

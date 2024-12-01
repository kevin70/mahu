package cool.houge.mahu.remote.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/// 下单响应.
///
/// @author ZY (kzou227@qq.com)
@Data
public class JsapiPrepayResult {

    /// 错误码
    private String code;
    /// 描述
    private String message;
    /// 商户申请的公众号对应的 AppID
    private String appid;
    /// 时间戳
    private String timeStamp;
    /// 随机字符串
    private String nonceStr;
    /// 签名类型，默认为RSA，仅支持RSA
    private String signType = "RSA";
    /// 小程序下单接口返回的 prepay_id 参数值
    private String packageValue;
    /// 签名
    private String paySign;
    /// 预支付交易会话标识
    @JsonProperty("prepay_id")
    private String prepayId;
}

package cool.houge.mahu.remote.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

/// 微信支付下单
///
/// @author ZY (kzou227@qq.com)
@Data
public class PrepayPayload {
    /// 公众号 ID
    private String appid;
    /// 直连商户号
    private String mchid;
    /// 描述
    private String description;
    /// 商户系统内部订单号，只能是数字、大小写字母_-*且在同一个商户号下唯一
    @JsonProperty("out_trade_no")
    private String outTradeNo;
    /// 订单失效时间，遵循**RFC3339**标准格式
    ///
    /// 示例：`2015-05-20T13:29:35+08:00`
    @JsonProperty("time_expire")
    private ZonedDateTime timeExpire;
    /// 附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用，实际情况下只有支付完成状态才会返回该字段
    private String attach;
    /// 异步接收微信支付结果通知的回调地址，通知URL必须为外网可访问的URL，不能携带参数
    @JsonProperty("notify_url")
    private String notifyUrl;
    /// 订单金额信息
    private Amount amount;
    /// 支付者信息
    private Payer payer;
    /// 支付场景描述.
    @JsonProperty("scene_info")
    private SceneInfo sceneInfo;

    /// 订单金额信息
    @Data
    public static class Amount {

        /// 订单总金额，单位为分
        private Integer total;
        /// CNY：人民币，境内商户号仅支持人民币
        private String currency;
    }

    /// 支付者信息
    @Data
    public static class Payer {
        /// 用户在普通商户AppID下的唯一标识
        ///
        /// 下单前需获取到用户的OpenID，详见
        /// [OpenID获取](https://pay.weixin.qq.com/docs/merchant/development/glossary/parameter.html)
        private String openid;
    }

    /// 支付场景描述
    @Data
    public static class SceneInfo {
        /// 用户的客户端IP，支持IPv4和IPv6两种格式的IP地址
        @JsonProperty("payer_client_ip")
        private String payerClientIp;
    }
}

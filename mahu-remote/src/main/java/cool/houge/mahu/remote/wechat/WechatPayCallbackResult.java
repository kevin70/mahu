package cool.houge.mahu.remote.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

/// 微信支付回调结果
///
/// @author ZY (kzou227@qq.com)
@Data
public class WechatPayCallbackResult {

    /// 直连商户申请的公众号或移动应用AppID.
    @JsonProperty("AppID")
    private String appid;
    /// 商户的商户号，由微信支付生成并下发.
    private String mchid;
    /// 商户系统内部订单号，可以是数字、大小写字母_-*的任意组合且在同一个商户号下唯一.
    @JsonProperty("out_trade_no")
    private String outTradeNo;
    /// 微信支付系统生成的订单号.
    @JsonProperty("transaction_id")
    private String transactionId;
    /// 交易类型，枚举值.
    ///
    ///  - `JSAPI`：公众号支付
    ///  - `NATIVE`：扫码支付
    ///  - `App`：App支付
    ///  - `MICROPAY`：付款码支付
    ///  - `MWEB`：H5支付
    ///  - `FACEPAY`：刷脸支付
    @JsonProperty("trade_type")
    private String tradeType;
    /// 交易状态，枚举值.
    ///
    ///  - `SUCCESS`：支付成功
    ///  - `REFUND`：转入退款
    ///  - `NOTPAY`：未支付
    ///  - `CLOSED`：已关闭
    ///  - `REVOKED`：已撤销（付款码支付）
    ///  - `USERPAYING`：用户支付中（付款码支付）
    ///  - `PAYERROR`：支付失败(其他原因，如银行返回失败)
    @JsonProperty("trade_state")
    private String tradeState;
    /// 交易状态描述.
    @JsonProperty("trade_state_desc")
    private String tradeStateDesc;
    /// 银行类型，采用字符串类型的银行标识。银行标识请参考
    /// [《银行类型对照表》](https://pay.weixin.qq.com/docs/merchant/development/chart/bank-type.html)
    @JsonProperty("bank_type")
    private String bankType;
    /// 附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用，实际情况下只有支付完成状态才会返回该字段.
    private String attach;
    /// 支付完成时间，遵循**RFC3339**标准格式，格式为`yyyy-MM-DDTHH:mm:ss+TIMEZONE`
    @JsonProperty("success_time")
    private ZonedDateTime successTime;
    /// 支付者信息
    private Payer payer;
    /// 订单金额信息
    private Amount amount;
    /// 支付场景信息描述
    @JsonProperty("scene_info")
    private SceneInfo sceneInfo;
    /// 优惠功能，享受优惠时返回该字段
    @JsonProperty("promotion_detail")
    private PromotionDetail promotionDetail;

    @Data
    public static class Payer {

        /// 用户在直连商户AppID下的唯一标识
        private String openid;
    }

    @Data
    public static class Amount {

        /// 订单总金额，单位为分.
        private int total;
        /// 用户支付金额，单位为分.
        @JsonProperty("payer_total")
        private int payerTotal;
        /// CNY：人民币，境内商户号仅支持人民币
        private String currency;
        /// 用户支付币种
        @JsonProperty("payer_currency")
        private String payerCurrency;
    }

    @Data
    public static class SceneInfo {

        /// 终端设备号（门店号或收银设备ID）.
        @JsonProperty("device_id")
        private String deviceId;
    }

    @Data
    public static class PromotionDetail {

        /// 券ID.
        @JsonProperty("coupon_id")
        private String coupon_id;
        /// 优惠名称.
        private String name;
        /// 优惠范围，枚举值.
        ///
        ///  - `GLOBAL`：全场代金券
        ///  - `SINGLE`：单品优惠
        private String scope;
        /// 优惠类型，枚举值.
        ///
        ///  - `CASH`：充值型代金券
        ///  - `NOCASH`：免充值型代金券
        private String type;
        /// 优惠券面额.
        private int amount;
        /// 活动ID.
        @JsonProperty("stock_id")
        private String stockId;
        /// 微信出资，单位为分.
        @JsonProperty("wechatpay_contribute")
        private Integer wechatpayContribute;
        /// 商户出资，单位为分.
        @JsonProperty("merchant_contribute")
        private Integer merchantContribute;
        /// 其他出资，单位为分.
        @JsonProperty("other_contribute")
        private Integer otherContribute;
        /// CNY：人民币，境内商户号仅支持人民币.
        private String currency;
        /// 单品列表信息.
        @JsonProperty("goods_detail")
        private List<GoodsDetail> goodsDetail;
    }

    @Data
    public static class GoodsDetail {

        /// 商品编码.
        @JsonProperty("goods_id")
        private String goodsId;
        /// 用户购买的数量.
        private int quantity;
        /// 商品单价，单位为分.
        @JsonProperty("unit_price")
        private int unitPrice;
        /// 商品优惠金额.
        @JsonProperty("discount_amount")
        private int discountAmount;
        /// 商品备注信息.
        @JsonProperty("goods_remark")
        private String goodsRemark;
    }
}

package cool.houge.mahu.remote.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

/// [微信退款回调数据](https://pay.weixin.qq.com/docs/merchant/apis/refund/refunds/refund-result-notice.html)
///
/// @author ZY (kzou227@qq.com)
@Data
public class WechatRefundCallbackResult {

    /// 商户的商户号，由微信支付生成并下发.
    private String mchid;
    /// 商户系统内部订单号，可以是数字、大小写字母_-*的任意组合且在同一个商户号下唯一.
    @JsonProperty("out_trade_no")
    private String outTradeNo;
    /// 商户退款单号.
    @JsonProperty("out_refund_no")
    private String outRefundNo;
    /// 微信支付系统生成的订单号.
    @JsonProperty("transaction_id")
    private String transactionId;
    /// 微信退款单号.
    @JsonProperty("refund_id")
    private String refundId;
    /// 退款状态。
    ///
    /// 枚举值：
    ///  - `SUCCESS`：退款成功
    ///  - `CLOSED`：退款关闭
    ///  - `ABNORMAL`：退款异常
    @JsonProperty("refund_status")
    private String refundStatus;
    /// 支付完成时间，遵循rfc3339标准格式，格式为yyyy-MM-DDTHH:mm:ss+TIMEZONE.
    @JsonProperty("success_time")
    private ZonedDateTime successTime;
    /// 取当前退款单的退款入账方。
    ///
    ///  1. 退回银行卡：{银行名称}{卡类型}{卡尾号}
    ///  1. 退回支付用户零钱: 支付用户零钱
    ///  1. 退还商户: 商户基本账户、商户结算银行账户
    ///  1. 退回支付用户零钱通：支付用户零钱通
    @JsonProperty("user_received_account")
    private String userReceivedAccount;
    /// 金额信息.
    private Amount amount;

    @Data
    public static class Amount {

        /// 订单总金额，单位为分.
        private int total;
        /// 用户支付金额，单位为分.
        @JsonProperty("payer_total")
        private int payerTotal;
        /// 退款金额，币种的最小单位，只能为整数，不能超过原订单支付金额，如果有使用券，后台会按比例退。
        private int refund;
        /// 退款给用户的金额，不包含所有优惠券金额.
        @JsonProperty("payer_refund")
        private int payerRefund;
    }
}

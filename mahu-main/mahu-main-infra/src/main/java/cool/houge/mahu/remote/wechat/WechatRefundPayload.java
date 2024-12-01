package cool.houge.mahu.remote.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 退款 <a href="https://pay.weixin.qq.com/docs/merchant/apis/refund/refunds/create.html">https://pay.weixin.qq.com/docs/merchant/apis/refund/refunds/create.html</a>.
 * @author ZY (kzou227@qq.com)
 */
@Data
public class WechatRefundPayload {

    /**
     * 商户订单号.
     */
    @JsonProperty("out_trade_no")
    private String outTradeNo;
    /**
     * 商户退款单号.
     */
    @JsonProperty("out_refund_no")
    private String outRefundNo;
    /**
     * 退款原因.
     */
    private String reason;
    /**
     * 【金额信息】 订单金额信息.
     */
    private Amount amount;
    /**
     * 【退款结果回调url】 异步接收微信支付退款结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。 如果参数中传了notify_url，则商户平台上配置的回调地址将不会生效，优先回调当前传的这个地址。
     */
    @JsonProperty("notify_url")
    private String notifyUrl;

    @Data
    public static class Amount {

        /**
         * 【退款金额】 退款金额，单位为分，只能为整数，不能超过原订单支付金额。
         */
        private Integer refund;
        /**
         * 【原订单金额】 原支付交易的订单总金额，单位为分，只能为整数。
         */
        private Integer total;
        /**
         * 【退款币种】 符合ISO 4217标准的三位字母代码，目前只支持人民币：CNY。
         */
        private String currency = "CNY";
    }
}

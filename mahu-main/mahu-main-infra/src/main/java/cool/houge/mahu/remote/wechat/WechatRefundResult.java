package cool.houge.mahu.remote.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/// 退款
/// [微信文档](https://pay.weixin.qq.com/docs/merchant/apis/refund/refunds/create.html)
///
/// @author ZY (kzou227@qq.com)
@Data
public class WechatRefundResult {

    /// 代码.
    private String code;
    /// 消息描述.
    private String message;
    /// 【微信支付退款号】 微信支付退款号.
    @JsonProperty("refund_id")
    private String refundId;
    /// 【商户退款单号】 商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔.
    @JsonProperty("out_refund_no")
    private String outRefundNo;
    /// 【微信支付订单号】 微信支付交易订单号.
    @JsonProperty("transaction_id")
    private String transactionId;
    /// 【商户订单号】 原支付交易对应的商户订单号.
    @JsonProperty("out_trade_no")
    private String outTradeNo;
    /// 【退款渠道】 退款渠道.
    ///
    /// 可选取值：
    ///  - `ORIGINAL`: 原路退款
    ///  - `BALANCE`: 退回到余额
    ///  - `OTHER_BALANCE`: 原账户异常退到其他余额账户
    ///  - `OTHER_BANKCARD`: 原银行卡异常退到其他银行卡
    private String channel;
    /// 【退款入账账户】 取当前退款单的退款入账方，有以下几种情况：
    ///
    ///  1. 退回银行卡：{银行名称}{卡类型}{卡尾号}
    ///  1. 退回支付用户零钱:支付用户零钱
    ///  1. 退还商户:商户基本账户商户结算银行账户
    ///  1. 退回支付用户零钱通:支付用户零钱通
    @JsonProperty("user_received_account")
    private String userReceivedAccount;
    /// 【退款成功时间】 退款成功时间，退款状态status为SUCCESS（退款成功）时，返回该字段。
    /// 遵循rfc3339标准格式，格式为YYYY-MM-DDTHH:mm:ss+TIMEZONE，YYYY-MM-DD表示年月日，T出现在字符串中，
    /// 表示time元素的开头，HH:mm:ss表示时分秒，TIMEZONE表示时区（+08:00表示东八区时间，领先UTC 8小时，即北京时间）。
    ///
    /// 例如：2015-05-20T13:29:35+08:00表示，北京时间2015年5月20日13点29分35秒。
    @JsonProperty("success_time")
    private String successTime;
    /// 【退款创建时间】 退款受理时间，遵循rfc3339标准格式，格式为YYYY-MM-DDTHH:mm:ss+TIMEZONE，
    /// YYYY-MM-DD表示年月日，T出现在字符串中，表示time元素的开头，HH:mm:ss表示时分秒，
    /// TIMEZONE表示时区（+08:00表示东八区时间，领先UTC 8小时，即北京时间）。
    ///
    /// 例如：2015-05-20T13:29:35+08:00表示，北京时间2015年5月20日13点29分35秒。
    @JsonProperty("create_time")
    private String createTime;
    /// 【退款状态】 退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，可前往商户平台（pay.weixin.qq.com）-交易中心，手动处理此笔退款。
    ///
    /// 可选取值：
    ///  - `SUCCESS`: 退款成功
    ///  - `CLOSED`: 退款关闭
    ///  - `PROCESSING`: 退款处理中
    ///  - `ABNORMAL`: 退款异常
    private String status;
    /// 【资金账户】 退款所使用资金对应的资金账户类型
    ///
    /// 可选取值：
    ///  - `UNSETTLED`: 未结算资金
    ///  - `AVAILABLE`: 可用余额
    ///  - `UNAVAILABLE`: 不可用余额
    ///  - `OPERATION`: 运营户
    ///  - `BASIC`: 基本账户（含可用余额和不可用余额）
    ///  - `ECNY_BASIC`: 数字人民币基本账户
    @JsonProperty("funds_account")
    private String fundsAccount;
}

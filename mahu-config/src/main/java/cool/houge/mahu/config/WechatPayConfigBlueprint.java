package cool.houge.mahu.config;

import io.helidon.builder.api.Description;
import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

import static cool.houge.mahu.config.WechatPayConfigBlueprint.PREFIX;

/// 微信支付配置
///
/// @author ZY (kzou227@qq.com)
@Prototype.Blueprint
@Prototype.Configured(PREFIX)
interface WechatPayConfigBlueprint {

    /// 默认前缀
    String PREFIX = "wechat-pay";

    ///
    // 微信下单基础[地址](https://pay.weixin.qq.com/docs/merchant/development/practices/cross-city-disaster-escalation-guidelines.html)
    ///
    /// - 【主域名】 <a href="https://api.mch.weixin.qq.com">https://api.mch.weixin.qq.com</a>
    /// - 【备域名】 <a href="https://api2.mch.weixin.qq.com">https://api2.mch.weixin.qq.com</a>
    @Option.Configured
    @Description("微信下单基础地址")
    String baseUri();

    @Option.Configured
    @Description("直连商户号")
    String mchid();

    @Option.Configured
    @Description("应用程序ID")
    String appid();

    @Option.Configured
    @Description("商户API证书序列号serial_no，用于声明所使用的证书")
    String serialNo();

    @Option.Configured
    @Description("私钥资源路径地址")
    String privateKeyResource();

    @Option.Configured
    @Description("通知回调地址")
    String notifyUrl();
}

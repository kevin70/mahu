package cool.houge.mahu.remote.wechat;

import cool.houge.mahu.TestBase;
import cool.houge.mahu.config.WechatPayConfig;
import cool.houge.util.NanoIdUtils;
import io.helidon.common.config.GlobalConfig;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

/**
 * @author ZY (kzou227@qq.com)
 */
class WechatPayClientTest extends TestBase {

    @Inject
    WechatPayClient wechatPayClient;

    WechatPayConfig config() {
        return WechatPayConfig.create(GlobalConfig.config().get(WechatPayConfig.PREFIX));
    }

    @Test
    void prepayJsapi() {
        var payload = new PrepayPayload()
                .setNotifyUrl("https://ec-api.crosshealth.club/payments/wechat-callback")
                .setAmount(new PrepayPayload.Amount().setTotal(1))
                .setAppid("wx83333d7720183438")
                .setMchid("1667418951")
                .setDescription("测试订单 " + LocalDate.now())
                .setOutTradeNo(NanoIdUtils.randomNanoId())
                .setPayer(new PrepayPayload.Payer().setOpenid("odng_5SoIPelKzA1uMinCHeCwhlw"));
        var result = wechatPayClient.prepayJsapi(config(), payload);
        System.out.println(result);
    }

    @Test
    void refund() {
        var payload = new WechatRefundPayload()
                .setNotifyUrl("https://ec-api.crosshealth.club/payments/wechat-callback")
                .setAmount(new WechatRefundPayload.Amount().setRefund(1).setTotal(1))
                .setOutRefundNo(NanoIdUtils.randomNanoId())
                .setOutTradeNo(NanoIdUtils.randomNanoId());
        var result = wechatPayClient.refund(config(), payload);
        System.out.println(result);
    }
}

package cool.houge.mahu.remote.wechat;

import com.github.f4b6a3.ulid.Ulid;
import cool.houge.mahu.config.WechatPayConfig;
import cool.houge.mahu.remote.TestBase;
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
                .setOutTradeNo(Ulid.fast().toString())
                .setPayer(new PrepayPayload.Payer().setOpenid("odng_5SoIPelKzA1uMinCHeCwhlw"));
        var result = wechatPayClient.prepayJsapi(config(), payload);
        System.out.println(result);
    }

    @Test
    void refund() {
        var payload = new WechatRefundPayload()
                .setNotifyUrl("https://ec-api.crosshealth.club/payments/wechat-callback")
                .setAmount(new WechatRefundPayload.Amount().setRefund(1).setTotal(1))
                .setOutRefundNo(Ulid.fast().toString())
                .setOutTradeNo(Ulid.fast().toString());
        var result = wechatPayClient.refund(config(), payload);
        System.out.println(result);
    }
}

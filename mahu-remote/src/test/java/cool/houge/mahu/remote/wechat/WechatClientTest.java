package cool.houge.mahu.remote.wechat;

import cool.houge.mahu.remote.TestBase;
import io.helidon.webclient.api.Proxy;
import io.helidon.webclient.api.WebClient;
import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit.core.config.LogLevel;
import io.specto.hoverfly.junit.dsl.HoverflyDsl;
import io.specto.hoverfly.junit.dsl.matchers.HoverflyMatchers;
import io.specto.hoverfly.junit5.HoverflyExtension;
import io.specto.hoverfly.junit5.api.HoverflyConfig;
import io.specto.hoverfly.junit5.api.HoverflyCore;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.HttpBodyConverter.json;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.instancio.Instancio.gen;

/// 微信 API 客户端
///
/// @author ZY (kzou227@qq.com)
@HoverflyCore(config = @HoverflyConfig(logLevel = LogLevel.DEBUG))
@ExtendWith(HoverflyExtension.class)
class WechatClientTest extends TestBase {

    WechatClient getWechatClient(Hoverfly hoverfly) {
        var client = new WechatClient();
        var proxy = Proxy.builder()
            .type(Proxy.ProxyType.HTTP)
            .host(hoverfly.getHoverflyConfig().getHost())
            .port(hoverfly.getHoverflyConfig().getProxyPort())
            .build();
        client.webClient =
            WebClient.builder().tls(b -> b.trustAll(true)).proxy(proxy).build();
        return client;
    }

    @Test
    void accessToken(Hoverfly hoverfly) {
        var payload = Instancio.create(WechatTokenPayload.class);
        var accessToken = gen().uuid().get().toString();

        var response = HoverflyDsl.response().body(json(Map.of("access_token", accessToken, "expires_in", 7200)));
        var simulate = dsl(service(HoverflyMatchers.contains("api.weixin.qq.com"))
            .get("/cgi-bin/token")
            .queryParam("grant_type", "client_credential")
            .queryParam("appid", payload.getAppid())
            .queryParam("secret", payload.getSecret())
            .willReturn(response));
        hoverfly.simulate(simulate);

        var wechatClient = getWechatClient(hoverfly);
        var result = wechatClient.accessToken(payload);

        assertThat(result.getErrcode()).isZero();
        assertThat(result.getAccessToken()).isEqualTo(accessToken);
        assertThat(result.getExpiresIn()).isEqualTo(7200);

        hoverfly.verifyAll();
    }

    @Test
    void accessToken_error(Hoverfly hoverfly) {
        var payload = Instancio.create(WechatTokenPayload.class);

        var response = HoverflyDsl.response().body(json(Map.of("errcode", 110)));
        var simulate = dsl(service(HoverflyMatchers.contains("api.weixin.qq.com"))
            .get("/cgi-bin/token")
            .queryParam("grant_type", "client_credential")
            .queryParam("appid", payload.getAppid())
            .queryParam("secret", payload.getSecret())
            .willReturn(response));
        hoverfly.simulate(simulate);

        var wechatClient = getWechatClient(hoverfly);
        assertThatExceptionOfType(WechatRemoteException.class)
            .isThrownBy(() -> wechatClient.accessToken(payload))
            .satisfiesAnyOf(e -> {
                assertThat(e).extracting(WechatRemoteException::getCode).isEqualTo(110);
            });

        hoverfly.verifyAll();
    }

    @Test
    void jscode2Session(Hoverfly hoverfly) {
        var payload = Instancio.create(Jscode2SessionPayload.class);
        var sessionKey = gen().string().hex().get();
        var unionid = gen().string().hex().get();
        var openid = gen().string().hex().get();

        var response = HoverflyDsl.response()
            .body(json(Map.of(
                "errcode", 0,
                "session_key", sessionKey,
                "unionid", unionid,
                "openid", openid)));
        var simulate = dsl(service(HoverflyMatchers.contains("api.weixin.qq.com"))
            .get("/sns/jscode2session")
            .queryParam("grant_type", "authorization_code")
            .queryParam("appid", payload.getAppid())
            .queryParam("secret", payload.getSecret())
            .queryParam("js_code", payload.getJsCode())
            .willReturn(response));
        hoverfly.simulate(simulate);

        var wechatClient = getWechatClient(hoverfly);
        var ret = wechatClient.jscode2Session(payload);
        assertThat(ret.getErrcode()).isZero();
        assertThat(ret.getSessionKey()).isEqualTo(sessionKey);
        assertThat(ret.getUnionid()).isEqualTo(unionid);
        assertThat(ret.getOpenid()).isEqualTo(openid);

        hoverfly.verifyAll();
    }
}

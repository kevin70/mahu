package cool.houge.mahu.admin;

import io.helidon.webclient.api.WebClient;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

public class DyRequestTest extends TestBase {

    @Inject
    WebClient webClient;

    @Test
    void requestProducts() {
        var data = "{\"page\": 0, \"page_size\": 15}";
        var req = new Req(
                "5642607", "MD5", System.currentTimeMillis(), UUID.randomUUID().toString(), data);
        var sign = getSign(req);
        try (var response = webClient
                .post("https://ecom.pangolin-sdk-toutiao.com/product/search")
                .submit(Map.of(
                        "app_id", req.appID,
                        "timestamp", req.timestamp,
                        "version", "1",
                        "sign", sign,
                        "sign_type", "MD5",
                        "req_id", req.reqID,
                        "data", req.data))) {
            var body = response.entity().as(String.class);
            System.out.println("body =============================================");
            System.out.println(body);
        }
    }

    static class Req {
        String appID;
        String signType;
        String version;
        long timestamp;
        String reqID;
        String data;
        String sign;

        Req(String appID, String signType, long timestamp, String reqID, String data) {
            this.appID = appID;
            this.signType = signType;
            this.timestamp = timestamp;
            this.reqID = reqID;
            this.data = data;
        }
    }

    static String md5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5 = md.digest(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : md5) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw null;
        }
    }

    public static String getSign(Req req) {
        final String secureKey = "AeiSVPAh8nqpXCsfrD9wI7yiZCdIjZv72AaBJ6qEyNU=";
        String signStr = String.format(
                "app_id=%s&data=%s&req_id=%s&timestamp=%d%s", req.appID, req.data, req.reqID, req.timestamp, secureKey);
        return md5(signStr);
    }
}

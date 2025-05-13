package cool.houge.mahu.remote.aliyun;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.helidon.webclient.api.WebClient;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/// 阿里云 HTTP 客户端
///
/// @author ZY (kzou227@qq.com)
public class AliyunClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public AliyunClient(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    /// 连接文件名称.
    ///
    /// @param segments 文件名称
    /// @param fileExt  文件后缀
    public String joinFileKey(List<String> segments, String fileExt) {
        var sb = new StringBuilder();
        for (String s : segments) {
            if (s == null || s.isEmpty()) {
                continue;
            }
            sb.append(s).append("/");
        }
        if (!sb.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        if (fileExt != null && !fileExt.isEmpty()) {
            sb.append(".").append(fileExt);
        }
        return sb.toString().replaceAll("/+", "/");
    }

    /// 客户端直接上传.
    ///
    /// @param request 直接上传请求
    public DirectUploadResponse directUploadSignature(DirectUploadRequest request) {
        var expiration = DateTimeFormatter.ISO_INSTANT.format(Instant.now().plusSeconds(request.getDurationSeconds()));

        var conditions = new ArrayList<>();
        conditions.add(Map.of("bucket", request.getBucket()));
        if (request.getMaxFileSize() > 0) {
            conditions.add(List.of("content-length-range", request.getMinFileSize(), request.getMaxFileSize()));
        }
        if (request.getContentTypes() != null && !request.getContentTypes().isEmpty()) {
            conditions.add(List.of("in", "$content-type", request.getContentTypes()));
        }
        conditions.add(List.of("eq", "$key", request.getKey()));

        var policy = Map.of("expiration", expiration, "conditions", conditions);
        String policyB64;
        try {
            var policyBytes = objectMapper.writeValueAsBytes(policy);
            policyB64 = Base64.getEncoder().encodeToString(policyBytes);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }

        var mac = hmacSHA1(request.getAccessKeySecret());
        byte[] signData = mac.doFinal(policyB64.getBytes(UTF_8));
        var signature = Base64.getEncoder().encodeToString(signData);

        return new DirectUploadResponse()
                .setKey(request.getKey())
                .setPolicy(policyB64)
                .setSignature(signature);
    }

    /// @param region  区域
    /// @param request 请求参数
    public AssumeRoleResponse assumeRole(String region, AssumeRoleRequest request) {
        var params = new LinkedHashMap<String, String>();
        params.put("Action", "AssumeRole");
        params.put("Version", "2015-04-01");
        params.put("Format", "JSON");
        params.put("SignatureMethod", "HMAC-SHA1");
        params.put("SignatureVersion", "1.0");
        params.put("AccessKeyId", request.getAccessKeyId());
        params.put("RoleArn", request.getRoleArn());
        params.put("SignatureNonce", signatureNonce());
        params.put("Timestamp", timestamp());
        if (nonNull(request.getDurationSeconds())) {
            params.put("DurationSeconds", request.getDurationSeconds().toString());
        }
        if (nonNull(request.getPolicy())) {
            params.put("Policy", request.getPolicy());
        }
        if (nonNull(request.getRoleSessionName())) {
            params.put("RoleSessionName", request.getRoleSessionName());
        }
        if (nonNull(request.getExternalId())) {
            params.put("ExternalId", request.getExternalId());
        }
        params.put("Signature", signature(request.getAccessKeySecret(), "POST", params));

        var req = webClient.post("https://sts.cn-hangzhou.aliyuncs.com");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            req.queryParam(entry.getKey(), entry.getValue());
        }

        var response = req.request(AssumeRoleResponse.class);
        return response.entity();
    }

    String signature(String accessKeySecret, String method, Map<String, String> signParams) {
        var entries = signParams.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();
        var canonQuery = new StringBuilder();
        for (Map.Entry<String, String> entry : entries) {
            canonQuery
                    .append('&')
                    .append(percentEncode(entry.getKey()))
                    .append('=')
                    .append(percentEncode(entry.getValue()));
        }

        String str = method + '&' + percentEncode("/") + '&' + percentEncode(canonQuery.substring(1));

        var mac = hmacSHA1(accessKeySecret + "&");
        byte[] signData = mac.doFinal(str.getBytes(UTF_8));
        return Base64.getEncoder().encodeToString(signData);
    }

    Mac hmacSHA1(String secret) {
        try {
            var mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(secret.getBytes(UTF_8), "HmacSHA1"));
            return mac;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }

    String signatureNonce() {
        return String.valueOf(Math.random());
    }

    String percentEncode(String value) {
        return value != null
                ? URLEncoder.encode(value, UTF_8)
                        .replace("+", "%20")
                        .replace("*", "%2A")
                        .replace("%7E", "~")
                : null;
    }

    String timestamp() {
        var df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(new SimpleTimeZone(0, "UTC"));
        return df.format(new Date());
    }
}

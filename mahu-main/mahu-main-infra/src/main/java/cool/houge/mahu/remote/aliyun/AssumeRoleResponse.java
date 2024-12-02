package cool.houge.mahu.remote.aliyun;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/// 该接口不能使用阿里云账号（主账号）调用，只能使用RAM用户或RAM角色调用，请确保已为调用者（RAM用户或RAM角色）授予STS的管理权限（AliyunSTSAssumeRoleAccess）
/// [文档](https://help.aliyun.com/zh/ram/developer-reference/api-sts-2015-04-01-assumerole#reference-clc-3sv-xdb)
///
/// @author ZY (kzou227@qq.com)
@Data
public class AssumeRoleResponse {

    /// 错误码.
    @JsonProperty("Code")
    private String code;
    /// 错误信息.
    @JsonProperty("Message")
    private String message;
    /// 请求 ID.
    @JsonProperty("RequestId")
    private String requestId;
    /// 角色扮演时的临时身份.
    @JsonProperty("AssumedRoleUser")
    private AssumedRoleUser assumedRoleUser;
    /// 访问凭证.
    @JsonProperty("Credentials")
    private Credentials credentials;

    @Data
    public static class AssumedRoleUser {
        /// 临时身份的 ID.
        @JsonProperty("AssumedRoleId")
        private String assumedRoleId;
        /// 临时身份的 ARN.
        @JsonProperty("Arn")
        private String arn;
    }

    @Data
    public static class Credentials {
        /// 安全令牌.
        @JsonProperty("SecurityToken")
        private String securityToken;
        /// Token到期失效时间（UTC时间）.
        @JsonProperty("Expiration")
        private String expiration;
        /// 访问密钥.
        @JsonProperty("AccessKeySecret")
        private String accessKeySecret;
        /// 访问密钥 ID.
        @JsonProperty("AccessKeyId")
        private String accessKeyId;
    }
}

package cool.houge.mahu.remote.aliyun;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 该接口不能使用阿里云账号（主账号）调用，只能使用RAM用户或RAM角色调用，请确保已为调用者（RAM用户或RAM角色）授予STS的管理权限（AliyunSTSAssumeRoleAccess）.
 * <a href="https://help.aliyun.com/zh/ram/developer-reference/api-sts-2015-04-01-assumerole#reference-clc-3sv-xdb">...</a>.
 * @author ZY (kzou227@qq.com)
 */
@Data
public class AssumeRoleRequest {
    /**
     * 阿里云访问密钥 ID.
     */
    @JsonProperty("AccessKeyId")
    private String accessKeyId;
    /**
     * 访问阿里云安全密钥.
     */
    @JsonProperty("AccessKeySecret")
    private String accessKeySecret;
    /**
     * Token有效期（单位：秒）.
     */
    @JsonProperty("DurationSeconds")
    private Integer durationSeconds;
    /**
     * 为 STS Token 额外添加的一个权限策略，进一步限制 STS Token 的权限.
     */
    @JsonProperty("Policy")
    private String policy;
    /**
     * 要扮演的RAM角色ARN.
     */
    @JsonProperty("RoleArn")
    private String roleArn;
    /**
     * 角色会话名称.
     */
    @JsonProperty("RoleSessionName")
    private String roleSessionName;
    /**
     * 角色外部ID.
     */
    @JsonProperty("ExternalId")
    private String externalId;
}

package cool.houge.mahu.config;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

/// 对象存储配置
///
/// @author ZY (kzou227@qq.com)
@Prototype.Blueprint
@Prototype.Configured(ConfigKeys.OSS)
interface OssConfigBlueprint {

    /// OSS 存储地址
    @Option.Configured
    String endpoint();

    /// OSS 访问安全键
    @Option.Configured
    String accessKey();

    /// OSS 访问安全密钥
    @Option.Configured
    String secretKey();

    /// OSS 存储地区代码
    @Option.Configured
    String region();

    /// OSS 存储桶
    @Option.Configured
    String bucket();

    /// 访问地址前缀，路径结尾需要`/`
    @Option.Configured
    String accessUrl();
}

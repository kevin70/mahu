package cool.houge.mahu.config;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

/// 对象存储（OSS）配置蓝图
///
/// 定义了 MinIO 对象存储服务的连接配置参数。
/// 对应配置前缀：`oss.*`
///
/// 示例配置（application.yaml）：
/// ```yaml
/// oss:
///   endpoint: "http://localhost:19000"
///   access-key: "minioadmin"
///   secret-key: "minioadmin"
///   region: "us-east-1"
///   bucket: "mahu-dev"
///   access-url: "http://localhost:19001"
/// ```
@Prototype.Blueprint
@Prototype.Configured(ConfigPrefixes.OSS)
interface OssConfigBlueprint {
    /// MinIO 服务端点地址（内部访问）
    @Option.Configured
    String endpoint();

    /// MinIO 访问密钥 ID
    @Option.Configured
    String accessKey();

    /// MinIO 访问密钥密码
    @Option.Configured
    String secretKey();

    /// MinIO 存储桶区域
    @Option.Configured
    String region();

    /// MinIO 存储桶名称
    @Option.Configured
    String bucket();

    /// MinIO 对象访问 URL（外部访问）
    /// 用于生成文件的公开访问链接
    @Option.Configured
    String accessUrl();
}

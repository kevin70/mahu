package cool.houge.mahu.config;

import static cool.houge.mahu.config.OssConfigBlueprint.PREFIX;
import io.helidon.builder.api.Description;
import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

/// 对象存储配置
///
/// @author ZY (kzou227@qq.com)
@Prototype.Blueprint
@Prototype.Configured(PREFIX)
interface OssConfigBlueprint {

    /**
     * 默认前缀.
     */
    String PREFIX = "oss";

    @Description("OSS存储地址")
    @Option.Configured
    String endpoint();

    @Description("OSS访问安全键")
    @Option.Configured
    String accessKey();

    @Description("OSS访问安全密钥")
    @Option.Configured
    String secretKey();

    @Description("OSS 存储地区代码")
    @Option.Configured
    String region();

    @Description("OSS 存储桶")
    @Option.Configured
    String bucket();
}

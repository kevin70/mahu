package cool.houge.mahu.config;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

import java.util.List;

import static cool.houge.mahu.config.OssConfigBlueprint.PREFIX;

/// 阿里云存储配置
///
/// @author ZY (kzou227@qq.com)
@Prototype.Blueprint(builderPublic = false, createEmptyPublic = false)
@Prototype.Configured(PREFIX)
interface OssConfigBlueprint {

    /**
     * 默认前缀.
     */
    String PREFIX = "oss";

    @Option.Configured
    String accessKeyId();

    @Option.Configured
    String accessKeySecret();

    @Option.Configured
    String endpoint();

    @Option.Configured
    String bucket();

    @Option.Configured
    String keyPrefix();

    @Option.Configured
    long minFileSize();

    @Option.Configured
    long maxFileSize();

    @Option.Configured
    int durationSeconds();

    @Option.Configured
    List<String> contentTypes();
}

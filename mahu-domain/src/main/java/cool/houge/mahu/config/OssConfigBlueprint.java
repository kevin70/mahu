package cool.houge.mahu.config;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

@Prototype.Blueprint
@Prototype.Configured(ConfigPrefixes.OSS)
interface OssConfigBlueprint {
    @Option.Configured
    String endpoint();

    @Option.Configured
    String accessKey();

    @Option.Configured
    String secretKey();

    @Option.Configured
    String region();

    @Option.Configured
    String bucket();

    @Option.Configured
    String accessUrl();
}

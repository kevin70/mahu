package cool.houge.mahu.config;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;
import java.time.Duration;

@Prototype.Blueprint
@Prototype.Configured(ConfigPrefixes.TOKEN)
interface TokenConfigBlueprint {
    @Option.Configured
    Duration accessExpires();

    @Option.Configured
    Duration refreshExpires();
}

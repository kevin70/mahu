package cool.houge.mahu.admin;

import io.helidon.health.HealthCheck;
import io.helidon.service.registry.Service.Singleton;
import io.helidon.webserver.observe.ObserveFeature;
import io.helidon.webserver.observe.config.ConfigObserver;
import io.helidon.webserver.observe.health.HealthObserver;
import io.helidon.webserver.observe.log.LogObserver;
import java.util.List;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;

/// 服务观测提供者
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
class ObserveFeatureProvider implements Supplier<ObserveFeature> {

    final List<HealthCheck> healthChecks;

    @Override
    public ObserveFeature get() {
        return ObserveFeature.just(
                HealthObserver.create(b -> b.details(true).addHealthChecks(healthChecks)),
                ConfigObserver.create(),
                LogObserver.create()
                //
                );
    }
}

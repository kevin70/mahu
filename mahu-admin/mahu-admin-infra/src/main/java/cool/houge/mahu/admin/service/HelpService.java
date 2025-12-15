package cool.houge.mahu.admin.service;

import cool.houge.mahu.config.ConfigPrefixes;
import cool.houge.mahu.config.InfoConfig;
import io.helidon.config.Config;
import io.helidon.service.registry.Service.Singleton;
import lombok.AllArgsConstructor;

/// 应用帮助服务
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class HelpService {

    private final Config config;

    /// 应用信息
    public InfoConfig info() {
        return InfoConfig.create(config.get(ConfigPrefixes.INFO));
    }
}

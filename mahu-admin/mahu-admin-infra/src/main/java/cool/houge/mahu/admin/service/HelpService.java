package cool.houge.mahu.admin.service;

import cool.houge.mahu.config.InfoConfig;
import io.helidon.common.config.Config;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 应用帮助服务
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class HelpService {

    @Inject
    Config config;

    /// 应用信息
    public InfoConfig info() {
        return InfoConfig.create(config.get(InfoConfig.PREFIX));
    }
}

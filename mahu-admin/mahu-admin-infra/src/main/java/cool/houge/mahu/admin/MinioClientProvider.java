package cool.houge.mahu.admin;

import cool.houge.mahu.config.ConfigPrefixes;
import cool.houge.mahu.config.OssConfig;
import io.helidon.config.Config;
import io.helidon.service.registry.Service;
import io.helidon.service.registry.Service.Singleton;
import io.minio.MinioClient;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

///
/// @author ZY (kzou227@qq.com)
@Singleton
class MinioClientProvider implements Supplier<MinioClient> {

    private static final Logger log = LogManager.getLogger(MinioClientProvider.class);
    final MinioClient v;

    MinioClientProvider(Config root) {
        var ossConfig = OssConfig.create(root.get(ConfigPrefixes.OSS));
        var builder = MinioClient.builder()
                .endpoint(ossConfig.endpoint())
                .credentials(ossConfig.accessKey(), ossConfig.secretKey());
        if (ossConfig.region() != null) {
            builder.region(ossConfig.region());
        }
        this.v = builder.build();
    }

    @Override
    public MinioClient get() {
        return v;
    }

    @Service.PreDestroy
    void destroy() {
        try {
            v.close();
        } catch (Exception e) {
            log.error("MinioClient 关闭错误", e);
        }
    }
}

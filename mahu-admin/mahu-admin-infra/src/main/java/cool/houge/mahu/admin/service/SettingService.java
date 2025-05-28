package cool.houge.mahu.admin.service;

import io.helidon.config.Config;
import io.minio.MinioClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

///
/// @author ZY (kzou227@qq.com)
@Singleton
public class SettingService {

    private final Config config;
    private final MinioClient minioClient;

    @Inject
    public SettingService(Config config, MinioClient minioClient) {
        this.config = config;
        this.minioClient = minioClient;
    }

    /// 构建 OSS 直接上传参数
    public MakeOssDirectUploadResult makeOssDirectUpload(MakeOssDirectUploadPayload payload) {
        return null;
    }
}

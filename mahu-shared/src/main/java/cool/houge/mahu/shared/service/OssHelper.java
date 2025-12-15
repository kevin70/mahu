package cool.houge.mahu.shared.service;

import static java.util.Objects.requireNonNull;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.config.ConfigPrefixes;
import cool.houge.mahu.config.OssConfig;
import io.helidon.config.Config;
import io.helidon.service.registry.Service;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/// 对象存储
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
class OssHelper {

    private final OssConfig ossConfig;
    private final MinioClient minioClient;

    OssHelper(Config root, MinioClient minioClient) {
        this.ossConfig = OssConfig.create(root.get(ConfigPrefixes.OSS));
        this.minioClient = minioClient;
    }

    /// 预签名上传
    ///
    /// @return 预签名上传的 URL
    String presignedUploadUrl(String objectKey, Map<String, String> queryParams) {
        requireNonNull(objectKey);
        requireNonNull(queryParams);
        return presignedUrl(Method.PUT, objectKey, queryParams);
    }

    /// 获取预签名 GET URL
    ///
    /// @param objectKey 对象的完整 key（含前缀）
    String presignedGetUrl(String objectKey) {
        return presignedUrl(Method.GET, objectKey, Map.of());
    }

    private String presignedUrl(Method method, String objectKey, Map<String, String> queryParams) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(method)
                    .bucket(ossConfig.bucket())
                    .object(objectKey)
                    .expiry(1, TimeUnit.DAYS)
                    .extraQueryParams(queryParams)
                    .build());
        } catch (Exception e) {
            throw new BizCodeException(BizCodes.UNAVAILABLE, "获取预签名上传的URL错误", e);
        }
    }
}

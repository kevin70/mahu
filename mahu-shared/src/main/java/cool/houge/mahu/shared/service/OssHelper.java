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

@Service.Singleton
class OssHelper {

    private final OssConfig ossConfig;
    private final MinioClient minioClient;

    OssHelper(Config root, MinioClient minioClient) {
        this.ossConfig = OssConfig.create(root.get(ConfigPrefixes.OSS));
        this.minioClient = minioClient;
    }

    String presignedUploadUrl(String objectKey, Map<String, String> queryParams) {
        requireNonNull(objectKey);
        requireNonNull(queryParams);
        return presignedUrl(Method.PUT, objectKey, queryParams);
    }

    String presignedGetUrl(String objectKey) {
        return presignedUrl(Method.GET, objectKey, Map.of());
    }

    String accessUrl(String objectKey) {
        return ossConfig.accessUrl() + objectKey;
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

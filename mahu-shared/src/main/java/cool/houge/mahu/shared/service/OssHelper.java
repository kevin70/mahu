package cool.houge.mahu.shared.service;

import static java.util.Objects.requireNonNull;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.config.ConfigPrefixes;
import cool.houge.mahu.config.OssConfig;
import io.helidon.config.Config;
import io.helidon.service.registry.Service;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.Http;
import io.minio.MinioClient;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/// MinIO 对象存储辅助类（内部使用）
///
/// 封装与 MinIO 客户端的交互，生成预签名 URL。
/// 预签名 URL 有效期为 1 天。
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

    /// 生成预签名上传 URL
    ///
    /// @param objectKey 对象键
    /// @param queryParams 额外的查询参数
    /// @return 预签名上传 URL（PUT 方法）
    String presignedUploadUrl(String objectKey, Map<String, String> queryParams) {
        requireNonNull(objectKey);
        requireNonNull(queryParams);
        return presignedUrl(Http.Method.PUT, objectKey, queryParams);
    }

    /// 生成预签名下载 URL
    ///
    /// @param objectKey 对象键
    /// @return 预签名下载 URL（GET 方法）
    String presignedGetUrl(String objectKey) {
        return presignedUrl(Http.Method.GET, objectKey, Map.of());
    }

    /// 生成直接访问 URL（仅限公开存储桶）
    ///
    /// @param objectKey 对象键
    /// @return 直接访问 URL
    String accessUrl(String objectKey) {
        return ossConfig.accessUrl() + objectKey;
    }

    /// 生成预签名 URL
    ///
    /// @param method HTTP 方法（GET 或 PUT）
    /// @param objectKey 对象键
    /// @param queryParams 额外的查询参数
    /// @return 预签名 URL（有效期 1 天）
    /// @throws BizCodeException 当调用 MinIO 失败时抛出异常
    private String presignedUrl(Http.Method method, String objectKey, Map<String, String> queryParams) {
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

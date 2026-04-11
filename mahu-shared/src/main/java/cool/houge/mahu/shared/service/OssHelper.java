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

    private static final Map<String, String> EMPTY_QUERY_PARAMS = Map.of();
    private static final int PRESIGNED_URL_EXPIRY_DAYS = 1;
    private static final String URL_PATH_SEPARATOR = "/";

    private final OssConfig ossConfig;
    private final MinioClient minioClient;

    OssHelper(Config root, MinioClient minioClient) {
        this.ossConfig = OssConfig.create(root.get(ConfigPrefixes.OSS));
        this.minioClient = requireNonNull(minioClient);
    }

    /// 生成预签名上传 URL
    ///
    /// @param objectKey 对象键
    /// @param queryParams 额外的查询参数
    /// @return 预签名上传 URL（PUT 方法）
    String presignedUploadUrl(String objectKey, Map<String, String> queryParams) {
        var key = requireObjectKey(objectKey);
        try {
            return presignedUrl(Http.Method.PUT, key, queryParams);
        } catch (IllegalArgumentException e) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "生成预签名上传URL参数非法", e);
        } catch (Exception e) {
            throw new BizCodeException(BizCodes.UNAVAILABLE, "获取预签名上传URL错误", e);
        }
    }

    /// 生成预签名下载 URL
    ///
    /// @param objectKey 对象键
    /// @return 预签名下载 URL（GET 方法）
    String presignedGetUrl(String objectKey) {
        var key = requireObjectKey(objectKey);
        try {
            return presignedUrl(Http.Method.GET, key, EMPTY_QUERY_PARAMS);
        } catch (IllegalArgumentException e) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "生成预签名下载URL参数非法", e);
        } catch (Exception e) {
            throw new BizCodeException(BizCodes.UNAVAILABLE, "获取预签名下载URL错误", e);
        }
    }

    /// 生成直接访问 URL（仅限公开存储桶）
    ///
    /// @param objectKey 对象键
    /// @return 直接访问 URL
    String accessUrl(String objectKey) {
        var key = requireObjectKey(objectKey);
        var accessBaseUrl = normalizeAccessBaseUrl(ossConfig.accessUrl());
        return key.startsWith(URL_PATH_SEPARATOR) ? accessBaseUrl + key.substring(1) : accessBaseUrl + key;
    }

    /// 生成预签名 URL
    ///
    /// @param method HTTP 方法（GET 或 PUT）
    /// @param objectKey 对象键
    /// @param queryParams 额外的查询参数
    /// @return 预签名 URL（有效期 1 天）
    private String presignedUrl(Http.Method method, String objectKey, Map<String, String> queryParams)
            throws Exception {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(method)
                .bucket(ossConfig.bucket())
                .object(objectKey)
                .expiry(PRESIGNED_URL_EXPIRY_DAYS, TimeUnit.DAYS)
                .extraQueryParams(normalizeQueryParams(queryParams))
                .build());
    }

    private static String requireObjectKey(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "objectKey 不能为空");
        }
        return objectKey;
    }

    private static String normalizeAccessBaseUrl(String accessUrl) {
        var normalized = accessUrl.strip();
        while (normalized.endsWith(URL_PATH_SEPARATOR)) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized + URL_PATH_SEPARATOR;
    }

    private static Map<String, String> normalizeQueryParams(Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return EMPTY_QUERY_PARAMS;
        }
        return Map.copyOf(queryParams);
    }
}

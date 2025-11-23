package cool.houge.mahu.shared.service;

import com.github.f4b6a3.uuid.codec.base.Base58BtcCodec;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.config.ConfigKeys;
import cool.houge.mahu.config.OssConfig;
import cool.houge.mahu.config.OssKind;
import cool.houge.mahu.shared.dto.PresignedUploadPayload;
import cool.houge.mahu.shared.dto.PresignedUploadResult;
import io.helidon.config.Config;
import io.helidon.service.registry.Service.Singleton;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/// 共享对象存储服务
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class SharedOssService {

    private static final Joiner SLASH_JOINER = Joiner.on("/");

    private final OssConfig ossConfig;
    private final MinioClient minioClient;

    public SharedOssService(Config root, MinioClient minioClient) {
        this.ossConfig = OssConfig.create(root.get(ConfigKeys.OSS));
        this.minioClient = minioClient;
    }

    /// 预签名上传
    ///
    /// @return 预签名上传的 URL
    public PresignedUploadResult presignedUpload(PresignedUploadPayload payload) {
        var objectName = objectName(payload);
        var url = presignedUrl(
                Method.PUT, objectName, Map.of("content-type", payload.getKind().getContentType()));

        // 预览图片
        var previewUrl = payload.getKind().isOpen() ? ossConfig.accessUrl() + objectName : presignedGetUrl(objectName);
        return new PresignedUploadResult(url, objectName, previewUrl);
    }

    /// 获取预签名 GET URL
    ///
    /// @param objectName 对象名称
    public String presignedGetUrl(String objectName) {
        return presignedUrl(Method.GET, objectName, Map.of());
    }

    private String presignedUrl(Method method, String objectName, Map<String, String> queryParams) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(method)
                    .bucket(ossConfig.bucket())
                    .object(objectName)
                    .expiry(1, TimeUnit.DAYS)
                    .extraQueryParams(queryParams)
                    .build());
        } catch (Exception e) {
            throw new BizCodeException(BizCodes.UNAVAILABLE, "获取预签名上传的URL错误", e);
        }
    }

    String objectName(PresignedUploadPayload payload) {
        var kind = payload.getKind();
        var fileName = payload.getFileName();
        Supplier<String> getName = () -> {
            var ext = Files.getFileExtension(fileName);
            var name = Base58BtcCodec.INSTANCE.encode(UUID.randomUUID());
            if (ext.isEmpty()) {
                return name;
            }
            return name + "." + ext;
        };

        if (kind == OssKind.ADMIN_AVATAR) {
            return SLASH_JOINER.join(kind.getPrefix(), payload.getAdminId(), getName.get());
        }
        return SLASH_JOINER.join(kind.getPrefix(), getName.get());
    }
}

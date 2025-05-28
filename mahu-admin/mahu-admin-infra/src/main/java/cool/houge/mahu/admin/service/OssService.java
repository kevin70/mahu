package cool.houge.mahu.admin.service;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.config.OssConfig;
import io.hypersistence.tsid.TSID;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/// OSS 对象存储
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class OssService {

    private final OssConfig ossConfig;
    private final MinioClient minioClient;

    @Inject
    public OssService(OssConfig ossConfig, MinioClient minioClient) {
        this.ossConfig = ossConfig;
        this.minioClient = minioClient;
    }

    /// 预签名上传
    ///
    /// @return 预签名上传的 URL
    public String presignedUpload(PresignedUploadPayload payload) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(ossConfig.bucket())
                    .object(objectName(payload))
                    .expiry(1, TimeUnit.DAYS)
                    .extraQueryParams(
                            Map.of("response-content-type", "application/json", "content-type", "image/*, video/*"))
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
            if (ext.isEmpty()) {
                return TSID.fast().toString();
            }
            return TSID.fast() + "." + ext;
        };

        switch (kind) {
            case BRAND -> {
                return Joiner.on('/').join(kind.getPrefix(), getName.get());
            }
            case ADMIN_AVATAR -> {
                return Joiner.on('/').join(kind.getPrefix(), payload.getAdminId(), getName.get());
            }
            case SHOP_ASSET -> {
                return Joiner.on('/').join(kind.getPrefix(), payload.getShopId(), getName.get());
            }
            default -> throw new BizCodeException(BizCodes.INVALID_ARGUMENT, "不支持的 OssKind: " + kind);
        }
    }
}

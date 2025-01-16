package cool.houge.mahu.admin.service;

import com.github.f4b6a3.ulid.Ulid;
import com.google.common.io.Files;
import cool.houge.mahu.config.OssConfig;
import cool.houge.mahu.remote.aliyun.AliyunClient;
import cool.houge.mahu.remote.aliyun.DirectUploadRequest;
import io.helidon.common.config.Config;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

///
/// @author ZY (kzou227@qq.com)
@Singleton
public class SettingService {

    @Inject
    Config config;

    @Inject
    AliyunClient aliyunClient;

    /// 构建 OSS 直接上传参数
    public MakeOssDirectUploadResult makeOssDirectUpload(MakeOssDirectUploadPayload payload) {
        var ossConfig = OssConfig.create(
                config.get(OssConfig.PREFIX + "." + payload.getKind().name()));
        var ext = Files.getFileExtension(payload.getFileName());
        var key = aliyunClient.joinFileKey(
                List.of(
                        ossConfig.keyPrefix(),
                        Optional.ofNullable(payload.getPrefixLimit()).orElse(""),
                        Ulid.fast().toString()),
                ext);
        var request = new DirectUploadRequest()
                .setBucket(ossConfig.bucket())
                .setKey(key)
                .setAccessKeyId(ossConfig.accessKeyId())
                .setAccessKeySecret(ossConfig.accessKeySecret())
                .setMinFileSize(ossConfig.minFileSize())
                .setMaxFileSize(ossConfig.maxFileSize())
                .setDurationSeconds(ossConfig.durationSeconds());
        var response = aliyunClient.directUploadSignature(request);

        return new MakeOssDirectUploadResult()
                .setAccessUrl(ossConfig.endpoint() + "/" + response.getKey())
                .setEndpoint(ossConfig.endpoint())
                .setKey(key)
                .setPolicy(response.getPolicy())
                .setAccessKeyId(ossConfig.accessKeyId())
                .setSignature(response.getSignature());
    }
}
